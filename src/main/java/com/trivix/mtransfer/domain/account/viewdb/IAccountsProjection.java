package com.trivix.mtransfer.domain.account.viewdb;

import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

public interface IAccountsProjection {
    IAccount getAccount(IAccountIdentifier accountIdentifier);
}
