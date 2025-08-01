package portfolioapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import portfolioapi.model.TransactionDTO;
import portfolioapi.service.TransactionsService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactions(@RequestParam(value = "portfolioIds") long[] ids,
                                                @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {


        return transactionsService.getTransactions(ids, startDate, endDate);
    }
}
