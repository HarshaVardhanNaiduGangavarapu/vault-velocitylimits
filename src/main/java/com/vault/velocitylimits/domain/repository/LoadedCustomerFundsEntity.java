package com.vault.velocitylimits.domain.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author harshagangavarapu
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "LoadedCustomerFunds")
public class LoadedCustomerFundsEntity {
    @Id
    @Column
    private Long id;
    @Column
    @JsonProperty("customer_id")
    private Long customerId;
    @Column
    @JsonProperty("load_amount")
    private double loadAmount;
    @Column
    private LocalDateTime time;
}
