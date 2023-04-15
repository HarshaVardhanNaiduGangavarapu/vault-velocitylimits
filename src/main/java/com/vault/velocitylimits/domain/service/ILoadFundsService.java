package com.vault.velocitylimits.domain.service;

/**
 * @author harshagangavarapu
 */
public interface ILoadFundsService {

    /**
     * This method handles the loading funds into customer accounts by reading `input.txt` which contains
     * necessary information and stores the loaded funds into DB if the transaction satisfies the `velocity limits`
     * and creates the transaction attempts info file `output.txt`.
     *
     */
    public void executeFundsLoadingToAccounts();
}
