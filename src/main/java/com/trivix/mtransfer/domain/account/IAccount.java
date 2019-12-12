package com.trivix.mtransfer.domain.account;

import com.trivix.common.core.events.IEvent;
import com.trivix.common.core.events.InvalidEventVersionException;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;
import com.trivix.mtransfer.domain.account.valueobjects.IBalance;

public interface IAccount {
    IAccountIdentifier getAccountId();
    IBalance getBalance();
    int getVersion();

    void applyEvent(IEvent event) throws InvalidEventVersionException;
}
