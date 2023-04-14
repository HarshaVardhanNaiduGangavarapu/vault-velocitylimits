package com.vault.velocitylimits.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadFundsAttempt {
    private Long id;
    private Long customerId;
    private boolean accepted;
}
