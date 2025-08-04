package portfolioapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import portfolioapi.model.TransactionDTO;
import portfolioapi.service.CsvFileProvider;
import portfolioapi.service.TransactionsService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionsControllerTest {

    @Mock
    private TransactionsService transactionsService;
    @Mock
    private CsvFileProvider csvFileProvider;

    @InjectMocks
    private TransactionsController target;

    /**
     * {@link TransactionsController#getTransactions(long[], LocalDate, LocalDate)}
     */
    @Test
    void shouldReturnCsvResponseSuccessfully() throws Exception {

        long[] ids = {1L, 2L};
        LocalDate startDate = LocalDate.of(2022, 8, 9);
        LocalDate endDate = LocalDate.of(2025, 2, 27);

        List<TransactionDTO> transactions = List.of(
                TransactionDTO.builder().build()
        );

        byte[] mockCsv = "mock,csv,data\n".getBytes();

        when(transactionsService.getTransactions(ids, startDate, endDate)).thenReturn(transactions);
        when(csvFileProvider.getTransactionsCsvFile(transactions)).thenReturn(mockCsv);

        ResponseEntity<byte[]> response = target.getTransactions(ids, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(mockCsv, response.getBody());
        assertNotNull(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("attachment; filename=transactions.csv",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));

        verify(transactionsService, times(1)).getTransactions(ids, startDate, endDate);
        verify(csvFileProvider, times(1)).getTransactionsCsvFile(transactions);
    }

    /**
     * {@link TransactionsController#getTransactions(long[], LocalDate, LocalDate)}
     */
    @Test
    void shouldReturn500WhenCsvGenerationFails() throws Exception {

        long[] ids = {99L};

        List<TransactionDTO> transactions = List.of();

        when(transactionsService.getTransactions(ids, null, null)).thenReturn(transactions);
        when(csvFileProvider.getTransactionsCsvFile(transactions))
                .thenThrow(new RuntimeException("Simulated failure"));

        ResponseEntity<byte[]> response = target.getTransactions(ids, null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(new String(response.getBody()).contains("Failed to generate CSV"));

        verify(transactionsService, times(1)).getTransactions(ids, null, null);
        verify(csvFileProvider, times(1)).getTransactionsCsvFile(transactions);
    }
}
