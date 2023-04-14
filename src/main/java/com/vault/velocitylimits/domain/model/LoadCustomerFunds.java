package com.vault.velocitylimits.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class LoadCustomerFunds {
    private Long id;
    private Long customerId;
    private double loadAmount;
    private LocalDateTime time;
}
