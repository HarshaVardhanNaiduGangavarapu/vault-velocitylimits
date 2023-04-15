package com.vault.velocitylimits.domain.runtime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vault.velocitylimits.domain.model.LoadFunds;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author harshagangavarapu
 */
public class LoadFundsDeserializer extends StdDeserializer<LoadFunds> {
    public LoadFundsDeserializer() {
       this(null);
    }
    public LoadFundsDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public LoadFunds deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Long id = node.get("id").asLong();
        Long customerId = node.get("customer_id").asLong();
        Double loadAmount = Double.valueOf(node.get("load_amount").asText().substring(1));
        LocalDateTime loadTime = LocalDateTime.parse(node.get("time").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        return new LoadFunds(id,customerId,loadAmount,loadTime);

    }
}
