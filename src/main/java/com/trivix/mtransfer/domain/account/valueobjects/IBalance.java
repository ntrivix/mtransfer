package com.trivix.mtransfer.domain.account.valueobjects;

import com.sun.istack.internal.NotNull;
import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;
import com.trivix.mtransfer.domain.account.BalanceChangeType;
import com.trivix.mtransfer.common.exceptions.ConcurrentBalanceChangeAttemptsException;
import com.trivix.mtransfer.common.exceptions.InsufficientFundsException;

import java.io.Serializable;
import java.util.Currency;

public interface IBalance extends Serializable, Iterable<IMoneyAmount> {
    IMoneyAmount changeBalance(@NotNull IMoneyAmount amount, BalanceChangeType balanceChangeType) throws InsufficientFundsException, ConcurrentBalanceChangeAttemptsException;

    IMoneyAmount getMoneyValue(Currency currency);
    
    
}
