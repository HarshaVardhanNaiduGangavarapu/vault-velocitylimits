package com.vault.velocitylimits.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoadFundsAttempt {
    private Long id;
    private Long customerId;
    private boolean accepted;
}
