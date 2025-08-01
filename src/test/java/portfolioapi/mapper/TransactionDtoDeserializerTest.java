package portfolioapi.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import portfolioapi.model.TransactionDTO;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TransactionDtoDeserializerTest {

    private TransactionDtoDeserializer deserializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deserializer = new TransactionDtoDeserializer();
        objectMapper = new ObjectMapper();
    }

    /**
     * {@link TransactionDtoDeserializer#deserialize(JsonParser, DeserializationContext)}
     */
    @Test
    void shouldCreateTransactionDTO() throws IOException {
        // language=JSON
        String json = """
        {
          "portfolio": { "shortName": "MyPortfolio" },
          "security": { "name": "MySecurity", "isinCode": "ISIN123456" },
          "currency": { "code": "USD" },
          "type": { "name": "Buy" },
          "transactionDate": "2025-08-01",
          "settlementDate": "2025-08-09",
          "quantity": "100",
          "unitPrice": "10.50",
          "tradeAmount": "1050"
        }
        """;

        JsonParser parser = objectMapper.createParser(json);
        DeserializationContext ctxt = mock(DeserializationContext.class);

        TransactionDTO result = deserializer.deserialize(parser, ctxt);

        assertNotNull(result);
        assertEquals("MyPortfolio", result.getPortfolioShortName());
        assertEquals("MySecurity", result.getSecurityName());
        assertEquals("ISIN123456", result.getSecurityIsinCode());
        assertEquals("USD", result.getCurrencyCode());
        assertEquals("Buy", result.getTypeName());
        assertEquals("2025-08-01", result.getTransactionDate());
        assertEquals("2025-08-09", result.getSettlementDate());
        assertEquals("100", result.getAmount());
        assertEquals("10.50", result.getUnitPrice());
        assertEquals("1050", result.getTradeAmount());
    }

    /**
     * {@link TransactionDtoDeserializer#deserialize(JsonParser, DeserializationContext)}
     */
    @Test
    void shouldCreateTransactionDtoWithMissingProperties() throws IOException {
        // language=JSON
        String json = """
        {
          "type": { "name": "Sell" },
          "transactionDate": "2023-08-10",
          "settlementDate": "2023-08-12",
          "quantity": "50",
          "unitPrice": "20.00",
          "tradeAmount": "1000"
        }
        """;

        JsonParser parser = objectMapper.createParser(json);
        DeserializationContext ctxt = mock(DeserializationContext.class);

        TransactionDTO dto = deserializer.deserialize(parser, ctxt);

        assertNotNull(dto);
        assertNull(dto.getPortfolioShortName());
        assertNull(dto.getSecurityName());
        assertNull(dto.getSecurityIsinCode());
        assertNull(dto.getCurrencyCode());
        assertEquals("Sell", dto.getTypeName());
        assertEquals("2023-08-10", dto.getTransactionDate());
        assertEquals("2023-08-12", dto.getSettlementDate());
        assertEquals("50", dto.getAmount());
        assertEquals("20.00", dto.getUnitPrice());
        assertEquals("1000", dto.getTradeAmount());
    }
}
