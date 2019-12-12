package com.trivix.mtransfer.common.exceptions;

import com.trivix.mtransfer.domain.account.valueobjects.IBalance;

public class AccountBalanceException extends RuntimeException {
    private IBalance balance;

    public AccountBalanceException(String message, IBalance balance) {
        super(message);
        this.balance = balance;
    }

    public IBalance getBalance() {
        return balance;
    }
}
