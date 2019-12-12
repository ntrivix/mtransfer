package com.trivix.mtransfer.domain.account;

import com.trivix.common.core.events.IEvent;
import com.trivix.common.core.events.InvalidEventVersionException;
import com.trivix.mtransfer.domain.account.events.BalanceChangedEventData;
import com.trivix.mtransfer.domain.account.valueobjects.AccountIdentifier;
import com.trivix.mtransfer.domain.account.valueobjects.Balance;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;
import com.trivix.mtransfer.domain.account.valueobjects.IBalance;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountAggregate implements IAccount {
    private IAccountIdentifier accountId;
    private IBalance balance;
    private AtomicInteger aggregateVersion;

    public AccountAggregate(IAccountIdentifier accountId, int lastEventId) {
        this.accountId = accountId;
        this.balance = new Balance();
        this.aggregateVersion = new AtomicInteger(lastEventId);
    }

    public AccountAggregate() {
        this.accountId = new AccountIdentifier();
        this.balance = new Balance();
        this.aggregateVersion = new AtomicInteger(-1);
    }

    @Override
    public IAccountIdentifier getAccountId() {
        return accountId;
    }

    @Override
    public IBalance getBalance() {
        return balance;
    }

    @Override
    public int getVersion() {
        return aggregateVersion.get();
    }
    
    @Override
    public void applyEvent(IEvent event) throws InvalidEventVersionException {
        if (event.getAggregateId() != accountId.getUUID())
            throw new IllegalArgumentException("Event id does not match aggregate id");
        
        if (!aggregateVersion.compareAndSet(event.getVersion() - 1, event.getVersion()))
            throw new InvalidEventVersionException(event.getVersion(), aggregateVersion.get() + 1, event);
        
        // Make sure that everything works even if order of the events is not correct.
        if (event.getDataType() == BalanceChangedEventData.class) {
            applyBalanceChangedEvent(event);
        }
    }
    
    private void applyBalanceChangedEvent(IEvent<BalanceChangedEventData> balanceChangedEvent) {
        balance.changeBalance(
                balanceChangedEvent.getData().getDeltaAmount(), 
                balanceChangedEvent.getData().getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountAggregate account = (AccountAggregate) o;
        return accountId.equals(account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}
