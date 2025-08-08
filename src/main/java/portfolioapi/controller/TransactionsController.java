package portfolioapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import portfolioapi.model.TransactionDTO;
import portfolioapi.service.CsvFileProvider;
import portfolioapi.service.TransactionsService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class TransactionsController {

    private final TransactionsService transactionsService;
    private final CsvFileProvider csvCreationService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService, CsvFileProvider csvCreationService) {
        this.transactionsService = transactionsService;
        this.csvCreationService = csvCreationService;
    }

    @GetMapping(value = "/transactions", produces = "text/csv")
    public  ResponseEntity<byte[]> getTransactions(@RequestParam(value = "portfolioIds") long[] ids,
                                                @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<TransactionDTO> transactions = transactionsService.getTransactions(ids, startDate, endDate);

        byte[] csvByteArray;
        try {
            csvByteArray = csvCreationService.getTransactionsCsvFile(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Failed to generate CSV: " + e.getMessage()).getBytes());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv");

        return new ResponseEntity<>(csvByteArray, headers, HttpStatus.OK);
    }
}
