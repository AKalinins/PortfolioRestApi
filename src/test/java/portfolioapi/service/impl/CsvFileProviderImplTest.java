package portfolioapi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import portfolioapi.model.TransactionDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvFileProviderImplTest {

    private CsvFileProviderImpl target;

    @BeforeEach
    void setUp() {
        target = new CsvFileProviderImpl();
    }

    /**
     * {@link CsvFileProviderImpl#getTransactionsCsvFile(List)}
     */
    @Test
    void shouldGenerateCsvWithSingleTransaction() throws IOException {
        TransactionDTO transaction = TransactionDTO.builder()
                .portfolioShortName("PORT123")
                .securityName("Company Inc.")
                .securityIsinCode("US88160R1014")
                .typeName("Buy")
                .currencyCode("USD")
                .transactionDate("2025-01-01")
                .settlementDate("2025-01-03")
                .amount("100")
                .unitPrice("850.50")
                .tradeAmount("85050.00")
                .build();

        byte[] csvBytes = target.getTransactionsCsvFile(List.of(transaction));
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

        assertNotNull(csvBytes);

        assertTrue(csvBytes.length > 0);

        //Values
        assertTrue(csvContent.contains("Company Inc."));
        assertTrue(csvContent.contains("PORT123"));
        assertTrue(csvContent.contains("US88160R1014"));
        assertTrue(csvContent.contains("Buy"));
        assertTrue(csvContent.contains("USD"));
        assertTrue(csvContent.contains("2025-01-01"));
        assertTrue(csvContent.contains("2025-01-03"));
        assertTrue(csvContent.contains("100"));
        assertTrue(csvContent.contains("850.50"));
        assertTrue(csvContent.contains("85050.00"));

        //Headers
        assertTrue(csvContent.startsWith("Portfolio Short Name"));
        assertTrue(csvContent.contains("Security Name"));
        assertTrue(csvContent.contains("Security ISIN Code"));
        assertTrue(csvContent.contains("Type Name"));
        assertTrue(csvContent.contains("Currency Code"));
        assertTrue(csvContent.contains("Transaction Date"));
        assertTrue(csvContent.contains("Settlement Date"));
        assertTrue(csvContent.contains("Amount"));
        assertTrue(csvContent.contains("Unit Price"));
        assertTrue(csvContent.contains("Trade Amount"));
    }

    /**
     * {@link CsvFileProviderImpl#getTransactionsCsvFile(List)}
     */
    @Test
    void shouldGenerateCsvWithEmptyList() throws IOException {
        byte[] csvBytes = target.getTransactionsCsvFile(List.of());
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 0);
        assertTrue(csvContent.contains("Portfolio Short Name"));
    }
}
