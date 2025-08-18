package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import portfolioapi.service.impl.model.TokenDto;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TokenProviderImplTest {

    private final RestClient restClient = mock(RestClient.class);
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
        tokenProvider = new TokenProviderImpl(restClient, objectMapper);

        ReflectionTestUtils.setField(tokenProvider, "openIdEndpoint", "http://localhost/token");
        ReflectionTestUtils.setField(tokenProvider, "userNameCredential", "user");
        ReflectionTestUtils.setField(tokenProvider, "passwordCredential", "pass");
    }

    /**
     * {@link TokenProviderImpl#getToken()}
     */
    @Test
    void shouldRequestNewTokenWhenTokenIsNull() {
        mockRestClient(dummyResponse);

        String token = tokenProvider.getToken();

        assertEquals("new-access-token", token);

        verify(restClient).post();
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

        verifyNoInteractions(restClient);
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

        mockRestClient(dummyResponse);

        String token = tokenProvider.getToken();

        assertEquals("new-access-token", token);

        verify(restClient).post();
    }

    private void mockRestClient(String responseBody) {
        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any(MultiValueMap.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(responseBody);
    }
}
