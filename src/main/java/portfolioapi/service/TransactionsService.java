package portfolioapi.service;

import portfolioapi.model.TransactionDTO;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsService {

    List<TransactionDTO> getTransactions(long[] ids, LocalDate startDate, LocalDate endDate);
}
