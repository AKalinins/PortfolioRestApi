package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.model.Transaction;
import portfolioapi.service.TokenProvider;
import portfolioapi.service.TransactionsService;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

@Component
public class TransactionsServiceImpl implements TransactionsService {

    private final WebClient webClient;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public TransactionsServiceImpl(WebClient webClient, TokenProvider tokenProvider, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Transaction> getTransactions(long[] ids, LocalDate startDate, LocalDate endDate) {

        Map<String, Object> payload = Map.of(
                "query", QUERY,
                "variables", getVariables(ids, startDate, endDate)
        );

        Mono<String> response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class);

        Mono<List<Transaction>> transactions = response.map(json -> {
            try {
                List<Transaction> transactionList = new ArrayList<>();
                JsonNode jsonNode = objectMapper.readTree(json);
                JsonNode portfoliosNode = jsonNode.get("data").get("portfoliosByIds");
                for (final JsonNode portfolioNode : portfoliosNode) {
                    JsonNode transactionsNode = portfolioNode.get("transactions");
                    transactionList.addAll(objectMapper.convertValue(transactionsNode,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Transaction.class)));
                }
                return transactionList;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse GraphQL response", e);
            }
        });

        return transactions.block();
    }

    private Map<String, Object> getVariables(long[] ids, LocalDate startDate, LocalDate endDate) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("ids", ids);
        variables.put("stratDate", startDate);
        variables.put("endDate", endDate);

        return variables;
    }

    private static final String QUERY = """
                    query Transactions($ids: [Long], $startDate: String, $endDate: String) {
                      portfoliosByIds( ids: $ids ) {
                            transactions(status:"OK", startDate: $startDate, endDate: $endDate) {
                                    portfolio:parentPortfolio {
                            name
                            shortName
                            type {
                              code
                              name
                            }
                            primaryContact {
                                    name
                                    contactId
                                    }
                            currency {
                              code:securityCode
                              name
                            }
                          }
                          type {
                            code: typeCode
                            name: typeName
                          }
                          transactionDate
                          settlementDate
                          paymentDate
                          tradeTime
                          extId
                          security {
                            name
                            code:securityCode
                            isinCode
                            type {
                              name
                              code
                            }
                          }
                          currency {
                            name
                            code:securityCode
                          }
                          account {
                            name
                            number
                          }
                          quantity: amount
                          unitPrice:unitPriceView
                          cost
                          cost2
                          tax
                          tax2
                          totalCost
                          tradeAmount
                          cashFlow
                          cashFlowInAccountCurrency
                          cashFlowInPortfolioCurrency
                          extInfo
                        }
                      }
                    }
                """;
}
