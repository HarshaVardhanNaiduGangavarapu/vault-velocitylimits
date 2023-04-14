package com.vault.velocitylimits.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author harshavardhannaidugangavarapu
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoadCustomerFunds {
    private Long id;
    @JsonProperty("customer_id")
    private Long customerId;
    @JsonProperty("load_amount")
    private double loadAmount;
    private LocalDateTime time;
}
