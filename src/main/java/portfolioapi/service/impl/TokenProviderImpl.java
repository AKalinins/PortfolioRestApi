package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.service.TokenProvider;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class TokenProviderImpl implements TokenProvider {

    @Value("${graphql.token.endpoint}")
    private String tokenEndpoint;
    @Value("${graphql.service.username}")
    private String userName;
    @Value("${graphql.service.password}")
    private String password;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private String token;
    private String refreshToken;
    private int expiresIn;
    private int refreshExpiresIn;

    @Autowired
    public TokenProviderImpl(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getToken() {

        // Form parameters
        Map<String, String> params = Map.of(
                "grant_type", "password",
                "client_id", "external-api",
                "username", userName,
                "password", password
        );

        String body = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        Mono<String> response = webClient.post()
                .uri(tokenEndpoint)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.block());
            this.token = jsonNode.get("access_token").asText();
            this.refreshToken = jsonNode.get("refresh_token").asText();
            this.expiresIn = jsonNode.get("expires_in").asInt();
            this.refreshExpiresIn = jsonNode.get("refresh_expires_in").asInt();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenId response", e);
        }

        return token;
    }
}
