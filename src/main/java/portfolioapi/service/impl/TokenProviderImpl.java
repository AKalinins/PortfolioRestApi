package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.service.TokenProvider;
import portfolioapi.service.impl.model.TokenDto;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class TokenProviderImpl implements TokenProvider {

    @Value("${openid.service.url}")
    private String openIdEndpoint;
    @Value("${graphql.service.username}")
    private String userName;
    @Value("${graphql.service.password}")
    private String password;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private TokenDto tokenDto;

    @Autowired
    public TokenProviderImpl(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getToken() {

        if (Objects.nonNull(tokenDto) && tokenDto.isValid()) {
            return tokenDto.getToken();
        } else if (Objects.nonNull(tokenDto) && tokenDto.isRefreshable()) {
            return refreshToken();
        } else {
            return requestNewToken();
        }
    }

    private String requestNewToken() {
        Mono<String> response = webClient.post()
                .uri(openIdEndpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", "external-api")
                        .with("username", userName)
                        .with("password", password))
                .retrieve()
                .bodyToMono(String.class);

        Mono<TokenDto> tokenDtoMono = response.map(json -> {
            try {
                JsonNode jsonNode = objectMapper.readTree(json);
                return TokenDto.builder()
                        .token(jsonNode.get("access_token").asText())
                        .refreshToken(jsonNode.get("refresh_token").asText())
                        .expiresIn(Integer.parseInt(jsonNode.get("expires_in").asText()))
                        .refreshExpiresIn(jsonNode.get("refresh_expires_in").asInt())
                        .tokenObtainedAt(System.currentTimeMillis())
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse OpenId response", e);
            }
        });
        this.tokenDto = tokenDtoMono.block();
        return Objects.nonNull(tokenDto) ? tokenDto.getToken() : "";
    }

    private String refreshToken() {
        return "";
    }
}
