package com.trivix.mtransfer.domain.account.exceptions;

import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

public class DuplicateAccountIdException extends RuntimeException {
    private IAccountIdentifier accountIdentifier;

    public DuplicateAccountIdException(IAccountIdentifier accountIdentifier) {
        super("User with id=" + accountIdentifier + " already exists!");
        this.accountIdentifier = accountIdentifier;
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }
}
