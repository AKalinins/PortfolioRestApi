package portfolioapi.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import portfolioapi.model.TransactionDTO;

import java.io.IOException;

@Component
public class TransactionDtoDeserializer extends JsonDeserializer<TransactionDTO> {

    @Override
    public TransactionDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        return TransactionDTO.builder()
                .portfolioShortName(getValue(rootNode.get("portfolio"), "shortName"))
                .securityName(getValue(rootNode.get("security"), "name"))
                .securityIsinCode(getValue(rootNode.get("security"), "isinCode"))
                .currencyCode(getValue(rootNode.get("currency"), "code"))
                .typeName(getValue(rootNode.get("type"), "name"))
                .transactionDate(getValue(rootNode, "transactionDate"))
                .settlementDate(getValue(rootNode, "settlementDate"))
                .amount(getValue(rootNode, "quantity"))
                .unitPrice(getValue(rootNode, "unitPrice"))
                .tradeAmount(getValue(rootNode, "tradeAmount"))
                .build();
    }

    private String getValue(JsonNode jsonNode, String valueName) {
        if (jsonNode != null && jsonNode.has(valueName)) {
            return  jsonNode.get(valueName).asText();
        }
        return null;
    }
}
