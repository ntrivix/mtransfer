package com.trivix.mtransfer.common.exceptions;

import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

public class AccountDoesNotExistException extends RuntimeException {
    private IAccountIdentifier accountIdentifier;

    public AccountDoesNotExistException(IAccountIdentifier accountIdentifier) {
        super("Account with id " + accountIdentifier.toString() + " does not exists!");
        this.accountIdentifier = accountIdentifier;
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }
}
