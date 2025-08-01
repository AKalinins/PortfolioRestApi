package portfolioapi.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import portfolioapi.mapper.TransactionDtoDeserializer;

@Getter
@Builder
@JsonDeserialize(using = TransactionDtoDeserializer.class)
public class TransactionDTO {
    private String portfolioShortName;
    private String securityName;
    private String securityIsinCode;
    private String typeName;
    private String currencyCode;
    private String transactionDate;
    private String settlementDate;
    private String amount;
    private String unitPrice;
    private String tradeAmount;
}
