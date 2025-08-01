package portfolioapi.service;

import portfolioapi.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsService {

    List<Transaction> getTransactions(long[] ids, LocalDate startDate, LocalDate endDate);
}
