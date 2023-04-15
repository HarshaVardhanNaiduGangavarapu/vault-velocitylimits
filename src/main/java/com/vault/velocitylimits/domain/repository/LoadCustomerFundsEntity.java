package com.vault.velocitylimits.domain.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "LoadedCustomerFunds")
@Getter
@Setter
/**
 * @author harshavardhannaidugangavarapu
 */
public class LoadCustomerFundsEntity {
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
