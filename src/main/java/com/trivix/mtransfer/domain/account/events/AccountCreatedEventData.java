package com.trivix.mtransfer.domain.account.events;

import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

public class AccountCreatedEventData {
    IAccountIdentifier accountIdentifier;

    public AccountCreatedEventData(IAccountIdentifier accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }
}
