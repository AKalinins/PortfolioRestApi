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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransactionsServiceImpl implements TransactionsService {

    private final WebClient webClient;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public TransactionsServiceImpl(WebClient webClient, TokenProvider tokenProvider) {
        this.webClient = webClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<Transaction> getTransactions(long[] ids, LocalDate startDate, LocalDate endDate) {

        Map<String, Object> payload = Map.of(
                "query", QUERY,
                "variables", getVariables(ids, startDate, endDate)
        );

        Mono<List<Transaction>> transactions = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        JsonNode usersNode = jsonNode.get("data").get("users");
                        return objectMapper.convertValue(usersNode,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Transaction.class));
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
