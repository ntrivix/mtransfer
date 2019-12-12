package com.trivix.mtransfer.domain.account.valueobjects;

import com.sun.istack.internal.NotNull;
import com.trivix.mtransfer.common.valueobjects.MoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.common.exceptions.ConcurrentBalanceChangeAttemptsException;
import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Balance implements IBalance {
    private ConcurrentHashMap<Currency, AtomicReference<IMoneyAmount>> balance;
    private static final int MAX_RETRIES = 20;

    public Balance() {
        balance = new ConcurrentHashMap<>();
    }
    
    @Override
    public IMoneyAmount changeBalance(@NotNull IMoneyAmount deltaAmount, AccountTransactionType accountTransactionType) throws ConcurrentBalanceChangeAttemptsException {
        validateAmount(deltaAmount);
        balance.putIfAbsent(
                deltaAmount.getCurrency(), 
                new AtomicReference<>(new MoneyAmount(deltaAmount.getCurrency(), BigDecimal.ZERO)));
            
        for (int i = 0; i < MAX_RETRIES; i++) {
            IMoneyAmount oldAmount = getMoneyValue(deltaAmount.getCurrency());
            IMoneyAmount newAmount = accountTransactionType == AccountTransactionType.DEPOSIT ? 
                    oldAmount.add(deltaAmount.getValue()) : 
                    oldAmount.subtract(deltaAmount.getValue());
            if (compareAndSet(oldAmount, newAmount))
                return newAmount;
        }
        
        throw new ConcurrentBalanceChangeAttemptsException(this, deltaAmount);
    }
    
    private void validateAmount(IMoneyAmount amount) 
    {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("You can only issue positive amount.");
    }

    /**
     * Atomically compare with oldValue object and set value to newValue
     * @param oldValue newValue will be applied only if oldValue == currentValue
     * @param newValue value to apply
     * @return
     */
    private boolean compareAndSet(IMoneyAmount oldValue, IMoneyAmount newValue) {
        // Put newValue if value for the given currency does not exist.
        AtomicReference<IMoneyAmount> oldMoneyValueRef = balance.putIfAbsent(
                newValue.getCurrency(), new AtomicReference<>(newValue));
        
        // If oldMoneyValue was equal to null, we already applied newValue.
        if (oldMoneyValueRef == null)
            return true;
        
        return oldMoneyValueRef.compareAndSet(oldValue, newValue);
    }
    
    @Override
    public IMoneyAmount getMoneyValue(Currency currency) {
        AtomicReference<IMoneyAmount> value = balance.getOrDefault(currency, null);
        if (value == null)
            return new MoneyAmount(currency, BigDecimal.ZERO);
        
        return value.get();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (AtomicReference<IMoneyAmount> value : balance.values()){
            IMoneyAmount moneyValue = value.get();
            stringBuilder.append(moneyValue.toString())
                    .append(System.lineSeparator());   
        }
        
        return stringBuilder.toString();
    }

    @Override
    public Iterator<IMoneyAmount> iterator() {
        return new BalanceMoneyAmountIterator(balance);
    }

    class BalanceMoneyAmountIterator implements Iterator<IMoneyAmount> {
        Iterator<AtomicReference<IMoneyAmount>> iterator;
 
        public BalanceMoneyAmountIterator(ConcurrentHashMap<Currency, AtomicReference<IMoneyAmount>> map)
        {
           iterator = map.values().iterator();
        }

        // returns false if next element does not exist 
        public boolean hasNext()
        {
            return iterator.hasNext();

        }

        // return current data and update pointer 
        public IMoneyAmount next()
        {
            return iterator.next().get();
        }

        // implement if needed 
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
