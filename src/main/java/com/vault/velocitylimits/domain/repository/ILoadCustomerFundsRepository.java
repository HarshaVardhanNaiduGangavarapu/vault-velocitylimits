package com.vault.velocitylimits.domain.repository;

import com.vault.velocitylimits.domain.model.LoadCustomerFunds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author harshavardhannaidugangavarapu
 */
public interface ILoadCustomerFundsRepository extends JpaRepository<LoadCustomerFundsEntity, Long> {
    List<LoadCustomerFunds> findAllByCustomerIdAndTimeBetween(Long customerId, LocalDateTime start, LocalDateTime end);

    long countByCustomerId(Long customerId);
}
