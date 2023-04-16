package com.vault.velocitylimits.domain.service;

/**
 * @author harshagangavarapu
 */
public interface ILoadFundsService {

    /**
     * This method handles the funds loading into customer accounts by reading `input.txt` which contains
     * transaction data and stores the loaded funds into h2 DB if the transaction satisfies the `velocity limits`
     * and creates the transaction attempts info file `output.txt`.
     */
    public void executeFundsLoadingToAccounts();
}
