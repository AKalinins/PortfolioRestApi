package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.service.impl.model.TokenDto;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TokenProviderImplTest {

    private final WebClient webClient = mock(WebClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TokenProviderImpl tokenProvider;

    //language=JSON
    private final String dummyResponse = """
        {
          "access_token": "new-access-token",
          "refresh_token": "new-refresh-token",
          "expires_in": "3600",
          "refresh_expires_in": 7200
        }
    """;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProviderImpl(webClient, objectMapper);

        ReflectionTestUtils.setField(tokenProvider, "openIdEndpoint", "http://localhost/token");
        ReflectionTestUtils.setField(tokenProvider, "userNameCredential", "user");
        ReflectionTestUtils.setField(tokenProvider, "passwordCredential", "pass");
    }

    /**
     * {@link TokenProviderImpl#getToken()}
     */
    @Test
    void shouldRequestNewTokenWhenTokenIsNull() {
        mockWebClient(dummyResponse);

        String token = tokenProvider.getToken();

        assertEquals("new-access-token", token);

        verify(webClient).post();
    }

    /**
     * {@link TokenProviderImpl#getToken()}
     */
    @Test
    void shouldReturnExistingTokenIfStillValid() {
        TokenDto tokenDto = TokenDto.builder()
                .token("existing-token")
                .refreshToken("refresh-token")
                .expiresIn(3600)
                .refreshExpiresIn(7200)
                .tokenObtainedAt(System.currentTimeMillis())
                .build();

        ReflectionTestUtils.setField(tokenProvider, "tokenDto", tokenDto);

        String token = tokenProvider.getToken();

        assertEquals("existing-token", token);

        verifyNoInteractions(webClient);
    }

    /**
     * {@link TokenProviderImpl#getToken()}
     */
    @Test
    void shouldRefreshTokenIfExpiredButRefreshable() {
        TokenDto expiredToken = TokenDto.builder()
                .token("expired-token")
                .refreshToken("refresh-token")
                .expiresIn(-1)  // expired
                .refreshExpiresIn(7200)
                .tokenObtainedAt(Instant.now().minusSeconds(4000).toEpochMilli())
                .build();

        ReflectionTestUtils.setField(tokenProvider, "tokenDto", expiredToken);

        mockWebClient(dummyResponse);

        String token = tokenProvider.getToken();

        assertEquals("new-access-token", token);

        verify(webClient).post();
    }

    private void mockWebClient(String responseBody) {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseBody));
    }
}
