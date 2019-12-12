package com.trivix.mtransfer.domain.account.queries;

import com.trivix.common.core.query.IQueryHandler;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.viewdb.IAccountsProjection;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountQueryHandler implements IQueryHandler<IAccount, AccountQuery> {
    
    IAccountsProjection accountRepository;

    @Inject
    public AccountQueryHandler(IAccountsProjection accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public IAccount executeQuery(AccountQuery query) {
        return accountRepository.getAccount(query.getAccountIdentifier());
    }
}
