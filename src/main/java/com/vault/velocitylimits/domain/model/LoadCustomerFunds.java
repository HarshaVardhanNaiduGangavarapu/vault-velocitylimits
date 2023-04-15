package com.vault.velocitylimits.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author harshavardhannaidugangavarapu
 */
@Setter
@Getter
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadCustomerFunds {
    private Long id;
    @JsonProperty("customer_id")
    private Long customerId;
    @JsonProperty("load_amount")
    private double loadAmount;
    private LocalDateTime time;
}
