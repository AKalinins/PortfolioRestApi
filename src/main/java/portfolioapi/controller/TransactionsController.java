package portfolioapi.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import portfolioapi.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/")
public class TransactionsController {

    private final TransactionsMock transactionsMock = new TransactionsMock();

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestParam(value = "portfolioIds") long[] ids,
                                             @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Transaction> listOfTransactions = transactionsMock.getTransactions(new Random().nextInt(5) + 1);
        return listOfTransactions;
    }
}
