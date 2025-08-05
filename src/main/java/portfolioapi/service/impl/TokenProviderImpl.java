package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.service.TokenProvider;
import portfolioapi.service.exception.JsonParsingException;
import portfolioapi.service.impl.model.TokenDto;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
public class TokenProviderImpl implements TokenProvider {

    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String EXTERNAL_API_VALUE = "external-api";
    private static final String ACCESS_TOKEN_VALUE = "access_token";
    private static final String EXPIRES_IN_VALUE = "expires_in";
    private static final String REFRESH_EXPIRES_IN_VALUE = "refresh_expires_in";

    @Value("${openid.service.url}")
    private String openIdEndpoint;
    @Value("${graphql.service.username}")
    private String userNameCredential;
    @Value("${graphql.service.password}")
    private String passwordCredential;

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

        log.info("New access token requested");

        BodyInserters.FormInserter<String> bodyInserters = BodyInserters
                .fromFormData(GRANT_TYPE_KEY, PASSWORD)
                .with(CLIENT_ID_KEY, EXTERNAL_API_VALUE)
                .with(USERNAME, userNameCredential)
                .with(PASSWORD, passwordCredential);

        this.tokenDto = sendRequest(bodyInserters);

        return Objects.nonNull(tokenDto) ? tokenDto.getToken() : "";
    }

    private String refreshToken() {

        log.info("Refresh token requested");

        BodyInserters.FormInserter<String> bodyInserters = BodyInserters
                .fromFormData(GRANT_TYPE_KEY, REFRESH_TOKEN)
                .with(CLIENT_ID_KEY, EXTERNAL_API_VALUE)
                .with(REFRESH_TOKEN, tokenDto.getRefreshToken());

        this.tokenDto = sendRequest(bodyInserters);

        return Objects.nonNull(tokenDto) ? tokenDto.getToken() : "";
    }

    private TokenDto sendRequest(BodyInserters.FormInserter<String> bodyInserters) {

        Mono<String> response = webClient.post()
                .uri(openIdEndpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(bodyInserters)
                .retrieve()
                .bodyToMono(String.class);

        Mono<TokenDto> tokenDtoMono = response.map(json -> {
            try {
                JsonNode jsonNode = objectMapper.readTree(json);
                return TokenDto.builder()
                        .token(jsonNode.get(ACCESS_TOKEN_VALUE).asText())
                        .refreshToken(jsonNode.get(REFRESH_TOKEN).asText())
                        .expiresIn(Integer.parseInt(jsonNode.get(EXPIRES_IN_VALUE).asText()))
                        .refreshExpiresIn(jsonNode.get(REFRESH_EXPIRES_IN_VALUE).asInt())
                        .tokenObtainedAt(System.currentTimeMillis())
                        .build();
            } catch (Exception e) {
                throw new JsonParsingException("Failed to parse OpenId response", e);
            }
        });

        return tokenDtoMono.block();
    }
}
