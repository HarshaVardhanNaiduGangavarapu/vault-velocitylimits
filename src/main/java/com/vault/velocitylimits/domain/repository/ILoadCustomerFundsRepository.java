package com.vault.velocitylimits.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author harshavardhannaidugangavarapu
 */
@Repository
public interface ILoadCustomerFundsRepository extends JpaRepository<LoadCustomerFundsEntity, Long> {
    @Query(value = "from LoadedCustomerFunds lcf where lcf.customerId=:customerId AND lcf.time>= :startDate AND lcf.time<=:endDate")
    List<LoadCustomerFundsEntity> findAllByCustomerIdAndTimeBetween(@Param("customerId") Long customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
