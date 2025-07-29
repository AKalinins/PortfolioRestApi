package portfolioapi.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class Transaction {

    private String portfolioShortName;
    private String securityName;
    private String isinCode;
    private String currencyCode;
    private long amount;
    private BigDecimal unitPrice;
    private BigDecimal tradeAmount;
    private String typeName;
    private LocalDate transactionDate;
    private LocalDate settlementDate;
}
