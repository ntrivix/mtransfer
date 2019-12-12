package com.trivix.mtransfer.domain.account.viewdb;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.istack.internal.NotNull;
import com.trivix.common.core.events.IEvent;
import com.trivix.common.core.events.IEventStore;
import com.trivix.common.core.events.InvalidEventVersionException;
import com.trivix.mtransfer.domain.account.AccountAggregate;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.events.AccountCreatedEventData;
import com.trivix.mtransfer.domain.account.exceptions.DuplicateAccountIdException;
import com.trivix.mtransfer.domain.account.valueobjects.AccountIdentifier;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryAccountsProjection implements IAccountsProjection {
    
    private ConcurrentHashMap<IAccountIdentifier, IAccount> accounts;
    private IEventStore eventStore;
    
    @Inject
    public InMemoryAccountsProjection(IEventStore eventStore) {
        accounts = new ConcurrentHashMap<>();
        this.eventStore = eventStore;
        eventStore.registerEventHandler(AccountCreatedEventData.class, this::addAccount);
    }

    @Override
    public IAccount getAccount(@NotNull IAccountIdentifier accountIdentifier) {
        return accounts.get(accountIdentifier);
    }

    public void addAccount(@NotNull IEvent<AccountCreatedEventData> accountCreatedEvent) {
        IAccountIdentifier accountIdentifier = new AccountIdentifier(accountCreatedEvent.getAggregateId());
        IAccount account = new AccountAggregate(accountIdentifier, accountCreatedEvent.getVersion());
        if (accounts.putIfAbsent(accountIdentifier, account) != null)
            throw new DuplicateAccountIdException(accountIdentifier);
        
        
        eventStore.registerEventHandler(accountIdentifier.getUUID(), event -> {
            try {
                account.applyEvent(event);
            } catch (InvalidEventVersionException e) {
                if (e.getProvidedId() > e.getExpectedId())
                    eventStore.getEventsAfterVersion(account.getAccountId().getUUID(), accountCreatedEvent.getVersion()).forEach(
                            newEvent -> {
                                try {
                                    account.applyEvent(newEvent); 
                                } catch (Exception e1) { 
                                    e1.printStackTrace();
                                }
                            }  
                    );
            }
        });
    }
}
