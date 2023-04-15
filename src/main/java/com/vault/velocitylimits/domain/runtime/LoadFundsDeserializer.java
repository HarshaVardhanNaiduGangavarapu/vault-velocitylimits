package com.vault.velocitylimits.domain.runtime;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.LongNode;
import com.vault.velocitylimits.domain.model.LoadCustomerFunds;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoadFundsDeserializer extends StdDeserializer<LoadCustomerFunds> {

    public LoadFundsDeserializer() {
       this(null);
    }
    public LoadFundsDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public LoadCustomerFunds deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Long id = node.get("id").asLong();
        Long customerId = node.get("customer_id").asLong();
        Double loadAmount = Double.valueOf(node.get("load_amount").asText().substring(1));
        LocalDateTime loadTime = LocalDateTime.parse(node.get("time").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        return new LoadCustomerFunds(id,customerId,loadAmount,loadTime);

    }
}
