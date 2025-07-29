package portfolioapi.controller;

import portfolioapi.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionsMock {

    public List<Transaction> getTransactions(int count) {

        List<Transaction> result = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            Transaction transaction = Transaction.builder()
                    .portfolioShortName("ShortName" + i)
                    .securityName("SecurityName" + i)
                    .isinCode("isinCode" + i)
                    .currencyCode("CurrencyCode" + i)
                    .amount(i * 1000L)
                    .unitPrice(BigDecimal.valueOf(i * 10L))
                    .tradeAmount(BigDecimal.valueOf(i * 100L))
                    .typeName("TypeName" + i)
                    .transactionDate(LocalDate.of(2025, i, i))
                    .settlementDate(LocalDate.of(2025, i, i))
                    .build();

            result.add(transaction);
        }

        return result;
    }
}
