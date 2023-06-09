package com.vault.velocitylimits.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author harshagangavarapu
 */
@Setter
@Getter
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadFunds {
    private Long id;
    @JsonProperty("customer_id")
    private Long customerId;
    @JsonProperty("load_amount")
    private double loadAmount;
    private LocalDateTime time;
}
