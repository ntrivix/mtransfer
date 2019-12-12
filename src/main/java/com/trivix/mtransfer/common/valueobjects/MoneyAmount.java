package com.trivix.mtransfer.common.valueobjects;

import com.sun.istack.internal.NotNull;
import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Objects;

/*
 * Immutable class that represents amount of money in one currency.
 */
public class MoneyAmount implements IMoneyAmount {
    
    private Currency currency;
    private BigDecimal value;
    private final transient NumberFormat numberFormat;

    public MoneyAmount(@NotNull Currency currency, @NotNull BigDecimal value) {
        this.currency = currency;
        this.value = value;
        
        numberFormat = NumberFormat.getCurrencyInstance();
        numberFormat.setCurrency(currency);
    }
    
    public MoneyAmount(@NotNull Currency currency, double value)
    {
        this(currency, new BigDecimal(value));
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }
    
    /*
     * Return new object that represents sum of current object and provided value.
     */
    @Override
    public IMoneyAmount add(@NotNull BigDecimal bigDecimal) {
        return new MoneyAmount(currency, value.add(bigDecimal));
    }

    @Override
    public IMoneyAmount subtract(@NotNull BigDecimal bigDecimal) {
        return new MoneyAmount(currency, value.subtract(bigDecimal));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IMoneyAmount that = (IMoneyAmount) o;
        return getCurrency().equals(that.getCurrency()) &&
                getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrency(), getValue());
    }

    public int compareTo(IMoneyAmount moneyValue) {
        if (moneyValue.getCurrency() != getCurrency())
            throw new UnsupportedOperationException("Can't compare two different currencies");
        
        return getValue().compareTo(moneyValue.getValue());
    }

    @Override
    public int compareTo(BigDecimal value) {
        return getValue().compareTo(value);
    }

    @Override
    public String toString() {
        return numberFormat.format(value);
    }
}
