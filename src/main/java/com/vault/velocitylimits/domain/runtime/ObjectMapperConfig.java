package com.vault.velocitylimits.domain.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vault.velocitylimits.domain.model.LoadFunds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author harshagangavarapu
 */
@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // register LoadFunds deserialize with mapper
        SimpleModule loadFundModule = new SimpleModule();
        loadFundModule.addDeserializer(LoadFunds.class, new LoadFundsDeserializer());
        objectMapper.registerModule(loadFundModule);

        // register time module for accurate serialization and deserialization
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
