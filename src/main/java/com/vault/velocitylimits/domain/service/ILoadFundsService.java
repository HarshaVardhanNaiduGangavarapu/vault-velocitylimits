package com.vault.velocitylimits.domain.service;

/**
 * @author harshagangavarapu
 */
public interface ILoadFundsService {
    /**
     * This method handles the loading of funds into customer accounts by reading the `input.txt` file, which contains
     * transaction data, and stores the loaded funds into an H2 database if the transaction satisfies the `velocity limits`.
     * It also creates a file called `output.txt`, which contains information about the transaction attempts.
     */
    void executeFundsLoadingToAccounts();
}
