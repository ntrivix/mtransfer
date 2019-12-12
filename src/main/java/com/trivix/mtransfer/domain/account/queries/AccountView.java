package com.trivix.mtransfer.domain.account.queries;

import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

public class AccountView {
    private IAccountIdentifier accountIdentifier;
    private HashMap<Currency, BigDecimal> moneyAmounts;

    public AccountView() {
    }

    public AccountView(IAccount account) {
        accountIdentifier = account.getAccountId();
        moneyAmounts = new HashMap<>();
        for (IMoneyAmount moneyAmount: account.getBalance()) {
            moneyAmounts.put(moneyAmount.getCurrency(), moneyAmount.getValue());
        }
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }

    public HashMap<Currency, BigDecimal> getMoneyAmounts() {
        return moneyAmounts;
    }
}
