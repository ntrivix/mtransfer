package com.trivix.mtransfer.domain.account.commands.changeBalance;

public enum ChangeAccountBalanceStatus {
    SUCCESSFUL,
    /**
     * Transaction already finished.
     */
    DUPLICATE_TRANSACTION, 
    
    INSUFFICIENT_MONEY,

    /**
     * Account does not exist.
     */
    INVALID_ACCOUNT,

    /**
     * Concurrency error, can't apply event.
     */
    INCONSISTENT_STATE
}
