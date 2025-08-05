package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.model.TransactionDTO;
import portfolioapi.service.TokenProvider;
import portfolioapi.service.TransactionsService;
import portfolioapi.service.exception.JsonParsingException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransactionsServiceImpl implements TransactionsService {

    @Value("${graphql.service.url}")
    private String graphQlUrl;

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
    public List<TransactionDTO> getTransactions(long[] ids, LocalDate startDate, LocalDate endDate) {

        Map<String, Object> payload = Map.of(
                "query", QUERY,
                "variables", getVariables(ids, startDate, endDate)
        );

        Mono<String> response = webClient.post()
                .uri(graphQlUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class);

        Mono<List<TransactionDTO>> transactions = response.map(json -> {
            try {
                List<TransactionDTO> transactionList = new ArrayList<>();
                JsonNode jsonNode = objectMapper.readTree(json);
                JsonNode portfoliosNode = jsonNode.get("data").get("portfoliosByIds");
                for (final JsonNode portfolioNode : portfoliosNode) {
                    JsonNode transactionsNode = portfolioNode.get("transactions");
                    transactionList.addAll(objectMapper.convertValue(transactionsNode,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, TransactionDTO.class)));
                }
                return transactionList;
            } catch (Exception e) {
                throw new JsonParsingException("Failed to parse GraphQL response", e);
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
                         shortName
                 }
                       transactionDate
                       settlementDate
                       security {
                         name
                         isinCode
                       }
                       currency {
                         code:securityCode
                       }
                      type {
                         name: typeName
                       }
                       quantity: amount
                       unitPrice:unitPriceView
                       tradeAmount
                     }
                   }
                 }
                """;
}
