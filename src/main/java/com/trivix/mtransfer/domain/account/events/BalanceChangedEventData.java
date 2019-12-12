package com.trivix.mtransfer.domain.account.events;

import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

public class BalanceChangedEventData {
    private IAccountIdentifier accountIdentifier;
    private IMoneyAmount deltaAmount;
    private IMoneyAmount newAmount;
    private AccountTransactionType type;
    private String description;
    
    public BalanceChangedEventData(IAccount account, IMoneyAmount deltaAmount, AccountTransactionType type, String description) {
        this.accountIdentifier = account.getAccountId();
        this.deltaAmount = deltaAmount;
        IMoneyAmount currentAmount = account.getBalance()
                .getMoneyValue(deltaAmount.getCurrency());
        if (type == AccountTransactionType.DEPOSIT)
            this.newAmount = currentAmount.add(deltaAmount.getValue());
        else
            this.newAmount = currentAmount.subtract(deltaAmount.getValue());
        this.type = type;
        this.description = description;
    }

    public BalanceChangedEventData(IAccountIdentifier accountIdentifier, IMoneyAmount deltaAmount, IMoneyAmount newAmount, AccountTransactionType type, String description) {
        this.accountIdentifier = accountIdentifier;
        this.deltaAmount = deltaAmount;
        this.newAmount = newAmount;
        this.type = type;
        this.description = description;
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }

    public IMoneyAmount getDeltaAmount() {
        return deltaAmount;
    }

    public IMoneyAmount getNewAmount() {
        return newAmount;
    }

    public AccountTransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
