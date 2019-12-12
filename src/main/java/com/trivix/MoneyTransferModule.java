package com.trivix;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.trivix.common.core.events.IEventStore;
import com.trivix.common.core.events.InMemoryEventStore;
import com.trivix.mtransfer.domain.account.AccountAggregate;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.viewdb.IAccountsProjection;
import com.trivix.mtransfer.domain.account.viewdb.InMemoryAccountsProjection;

public class MoneyTransferModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IAccount.class).to(AccountAggregate.class);
        bind(IEventStore.class).to(InMemoryEventStore.class);
        bind(IAccountsProjection.class).to(InMemoryAccountsProjection.class);
        bind(Gson.class).toInstance(new Gson());
    }
}
