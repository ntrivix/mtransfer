package com.trivix.mtransfer.domain.account.valueobjects;

import com.sun.istack.internal.NotNull;
import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.common.exceptions.ConcurrentBalanceChangeAttemptsException;

import java.io.Serializable;
import java.util.Currency;

public interface IBalance extends Serializable, Iterable<IMoneyAmount> {

    /**
     * Change balance for the given amount.
     * @param amount Non-negative amount
     * @param accountTransactionType
     * @return New moneyAmount for changed currency.
     * @throws ConcurrentBalanceChangeAttemptsException Change can't be applied due to many concurrent updates.
     * @throws IllegalArgumentException If amount is negative or null.
     */
    IMoneyAmount changeBalance(@NotNull IMoneyAmount amount, AccountTransactionType accountTransactionType) throws ConcurrentBalanceChangeAttemptsException;

    IMoneyAmount getMoneyValue(Currency currency);
    
    
}
