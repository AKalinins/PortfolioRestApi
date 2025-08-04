package portfolioapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import portfolioapi.model.TransactionDTO;
import portfolioapi.service.CsvFileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
@Slf4j
public class CsvFileProviderImpl implements CsvFileProvider {

    @Override
    public byte[] getTransactionsCsvFile(List<TransactionDTO> transactions) throws IOException {

        String[] headers = new String[] {
                "Portfolio Short Name",
                "Security Name",
                "Security ISIN Code",
                "Type Name",
                "Currency Code",
                "Transaction Date",
                "Settlement Date",
                "Amount",
                "Unit Price",
                "Trade Amount"
        };

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .builder()
                     .setHeader(headers)
                     .get())) {

            for (TransactionDTO dto : transactions) {
                csvPrinter.printRecord(
                        dto.getPortfolioShortName(),
                        dto.getSecurityName(),
                        dto.getSecurityIsinCode(),
                        dto.getTypeName(),
                        dto.getCurrencyCode(),
                        dto.getTransactionDate(),
                        dto.getSettlementDate(),
                        dto.getAmount(),
                        dto.getUnitPrice(),
                        dto.getTradeAmount()
                );
            }

            csvPrinter.flush();

        } catch (Exception e) {
            log.error("Exception during CSV file creation: {}", e.getMessage());
            throw e;
        }

        return outputStream.toByteArray();
    }
}
