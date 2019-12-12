package com.trivix.mtransfer.domain.account.queries;

import com.trivix.common.core.query.IQuery;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

public class AccountQuery implements IQuery<IAccount> {
    private IAccountIdentifier accountIdentifier;

    public AccountQuery(IAccountIdentifier accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }
}
