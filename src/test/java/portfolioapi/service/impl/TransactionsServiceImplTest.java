package portfolioapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import portfolioapi.model.TransactionDTO;
import portfolioapi.service.TokenProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceImplTest {

    @Mock
    private RestClient restClient;
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
        mockRestClient(dummyResponse);

        target = new TransactionsServiceImpl(restClient, tokenProvider, objectMapper);
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

        verify(restClient).post();
        verify(tokenProvider).getToken();
    }

    private void mockRestClient(String responseBody) {
        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("http://mocked-url.com")).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.header(eq("Authorization"), anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(Map.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(responseBody);
    }
}
