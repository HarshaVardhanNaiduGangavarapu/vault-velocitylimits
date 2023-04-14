package com.vault.velocitylimits.domain.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
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
    private Long customerId;
    @Column
    private double loadAmount;
    @Column
    private LocalDateTime time;
}
