package portfolioapi.service;

import portfolioapi.model.TransactionDTO;

import java.io.IOException;
import java.util.List;

public interface CsvFileProvider {

    byte[] getTransactionsCsvFile(List<TransactionDTO> transactions) throws IOException;
}
