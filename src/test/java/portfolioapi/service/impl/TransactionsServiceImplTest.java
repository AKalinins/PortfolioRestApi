package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.model.Transaction;
import portfolioapi.service.TokenProvider;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceImplTest {

    @Mock
    private WebClient webClient;
    @Mock
    private TokenProvider tokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TransactionsServiceImpl target;

    @Mock
    private WebClient.RequestBodyUriSpec uriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpecMock;
    @Mock
    private WebClient.RequestBodySpec bodySpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Test
    void shouldSuccessfullyReturnListOfTransactions() {

        Mono<String> response = Mono.just("""
                {
                  "data": {
                    "portfoliosByIds": [
                      {
                        "transactions": [
                          {
                            "portfolio": {
                              "shortName": "123B"
                            }
                          }
                        ]
                      }
                    ]
                  }
                }
                """);

        when(tokenProvider.getToken()).thenReturn("token");
        when(webClient.post()).thenReturn(uriSpecMock);
        when(uriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpecMock);
        when(bodySpecMock.header(eq("Authorization"), anyString())).thenReturn(bodySpecMock);
        doReturn(headersSpecMock).when(bodySpecMock).bodyValue(any());
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(response);

        target = new TransactionsServiceImpl(webClient, tokenProvider, objectMapper);

        List<Transaction> result = target.getTransactions(new long[]{1,2},LocalDate.now(), LocalDate.now());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("123B", result.get(0).getPortfolio().getShortName());

        verify(webClient).post();
        verify(tokenProvider).getToken();
        verify(uriSpecMock).contentType(MediaType.APPLICATION_JSON);
        verify(bodySpecMock).header("Authorization", "Bearer token");
        verify(bodySpecMock).bodyValue(any());
        verify(headersSpecMock).retrieve();
        verify(responseSpecMock).bodyToMono(String.class);
    }
}
