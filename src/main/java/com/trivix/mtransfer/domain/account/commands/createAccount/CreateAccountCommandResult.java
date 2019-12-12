package com.trivix.mtransfer.domain.account.commands.createAccount;

import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

public class CreateAccountCommandResult {
    IAccountIdentifier newAccountIdentifier;

    public CreateAccountCommandResult(IAccountIdentifier newAccountIdentifier) {
        this.newAccountIdentifier = newAccountIdentifier;
    }

    public IAccountIdentifier getNewAccountIdentifier() {
        return newAccountIdentifier;
    }
}
