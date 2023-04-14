package com.vault.velocitylimits.domain.service;

import com.vault.velocitylimits.domain.repository.LoadCustomerFundsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author harshavardhannaidugangavarapu
 */
@Service
public class LoadCustomerFundsService {
    @Autowired
    private LoadCustomerFundsRepository loadCustomerFundsRepository;

    public void loadFundsWithVelocityLimits(){

    }
}
