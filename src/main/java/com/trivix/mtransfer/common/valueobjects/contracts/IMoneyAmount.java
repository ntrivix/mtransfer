package com.trivix.mtransfer.common.valueobjects.contracts;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.util.Currency;

public interface IMoneyAmount extends Comparable<IMoneyAmount> {
    Currency getCurrency();

    BigDecimal getValue();

    /*
     * Return new object that represents sum of current object and provided value.
     */
    IMoneyAmount add(@NotNull BigDecimal bigDecimal);

    IMoneyAmount subtract(@NotNull BigDecimal bigDecimal);

    int compareTo(IMoneyAmount moneyValue);

    int compareTo(BigDecimal value);
}
