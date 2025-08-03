package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import portfolioapi.model.TransactionDTO;
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

    /**
     * {@link TransactionsServiceImpl#getTransactions(long[], LocalDate, LocalDate)}
     */
    @Test
    void shouldSuccessfullyReturnListOfTransactions() {
        // language=JSON
        String dummyResponse = """
                {
                   "data": {
                     "portfoliosByIds": [
                       {
                         "transactions": [
                           {
                             "portfolio": {
                               "shortName": "123B"
                             },
                             "transactionDate": "2025-02-27",
                             "settlementDate": "2025-08-09",
                             "security": {
                               "name": "MBGGR",
                               "isinCode": "ISIN123456"
                             },
                             "currency": {
                               "code": "EUR"
                             },
                             "type": {
                               "name": "Coupon"
                             },
                             "quantity": 1000000,
                             "unitPrice": 2.625,
                             "tradeAmount": 26250
                           }
                         ]
                       }
                     ]
                   }
                 }
                """;

        when(tokenProvider.getToken()).thenReturn("token");
        mockWebClient(dummyResponse);

        target = new TransactionsServiceImpl(webClient, tokenProvider, objectMapper);
        ReflectionTestUtils.setField(target, "graphQlUrl", "http://mocked-url.com");

        List<TransactionDTO> result = target.getTransactions(new long[]{1,2},LocalDate.now(), LocalDate.now());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("123B", result.get(0).getPortfolioShortName());
        assertEquals("MBGGR", result.get(0).getSecurityName());
        assertEquals("ISIN123456", result.get(0).getSecurityIsinCode());
        assertEquals("EUR", result.get(0).getCurrencyCode());
        assertEquals("Coupon", result.get(0).getTypeName());
        assertEquals("2025-02-27", result.get(0).getTransactionDate());
        assertEquals("2025-08-09", result.get(0).getSettlementDate());
        assertEquals("1000000", result.get(0).getAmount());
        assertEquals("2.625", result.get(0).getUnitPrice());
        assertEquals("26250", result.get(0).getTradeAmount());

        verify(webClient).post();
        verify(tokenProvider).getToken();
    }

    private void mockWebClient(String responseBody) {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("http://mocked-url.com")).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.header(eq("Authorization"), anyString())).thenReturn(bodySpec);
        doReturn(headersSpec).when(bodySpec).bodyValue(any());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(responseBody));
    }
}
