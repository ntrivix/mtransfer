package com.trivix.mtransfer.common.valueobjects;

import com.trivix.mtransfer.domain.account.BalanceChangeType;
import com.trivix.mtransfer.common.exceptions.ConcurrentBalanceChangeAttemptsException;
import com.trivix.mtransfer.common.exceptions.InsufficientFundsException;
import com.trivix.mtransfer.domain.account.valueobjects.IBalance;
import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;
import com.trivix.mtransfer.domain.account.valueobjects.Balance;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class BalanceTests {
    
    @Test
    void balanceGetNonInitializedCurrencyTest()
    {
        // Arrange.
        IBalance balance = new Balance();
        
        // Act.
        IMoneyAmount moneyValue = balance.getMoneyValue(Currency.getInstance("EUR"));
        
        // Assert.
        assertNotNull(moneyValue);
        assertEquals(BigDecimal.ZERO, moneyValue.getValue());
        assertEquals("EUR", moneyValue.getCurrency().getCurrencyCode());
    }
    
    @Test
    void changeBalanceNegativeAmount() {
        // Arrange.
        IBalance balance = new Balance();
        Currency currency = Currency.getInstance("EUR");
        IMoneyAmount addAmount = new MoneyAmount(currency, new BigDecimal(-100));

        // Act & Assert.
        assertThrows(IllegalArgumentException.class, () -> balance.changeBalance(addAmount, BalanceChangeType.DEPOSIT));
    }
    
    @Test
    void creditBalanceTest() throws InsufficientFundsException, ConcurrentBalanceChangeAttemptsException {
        // Arrange.
        IBalance balance = new Balance();
        Currency currency = Currency.getInstance("EUR");
        IMoneyAmount addAmount = new MoneyAmount(currency, new BigDecimal(100));
        
        // Act.
        IMoneyAmount newAmount = balance.changeBalance(addAmount, BalanceChangeType.DEPOSIT);
        
        // Assert.
        assertEquals(balance.getMoneyValue(currency), newAmount);
        assertEquals(0, newAmount.compareTo(new BigDecimal(100)));
    }

    @Test
    void debitBalanceTest() throws ConcurrentBalanceChangeAttemptsException, InsufficientFundsException {
        // Arrange.
        IBalance balance = new Balance();
        Currency currency = Currency.getInstance("EUR");
        IMoneyAmount addAmount = new MoneyAmount(currency, new BigDecimal(100));
        
        balance.changeBalance(addAmount, BalanceChangeType.DEPOSIT);
        
        // Act.
        IMoneyAmount newAmount = balance.changeBalance(addAmount, BalanceChangeType.WITHDRAW);

        // Assert.
        assertEquals(0, balance.getMoneyValue(currency).compareTo(BigDecimal.ZERO));
        assertEquals(0, newAmount.compareTo(BigDecimal.ZERO));
    }

    @Test
    void debitBalanceInsufficientFundsTest() {
        // Arrange.
        IBalance balance = new Balance();
        Currency currency = Currency.getInstance("EUR");
        MoneyAmount initialAmount = new MoneyAmount(currency, new BigDecimal(100));
        
        try {
            balance.changeBalance(initialAmount, BalanceChangeType.DEPOSIT);
        } catch (Exception e) {
            fail();
        }

        assertThrows(InsufficientFundsException.class, () -> balance.changeBalance(
                new MoneyAmount(currency, new BigDecimal(100.001f)), 
                BalanceChangeType.WITHDRAW));

        // Assert.
        assertEquals(0, balance.getMoneyValue(currency).compareTo(initialAmount));
    }
    
    @Test
    void toStringTest() throws InsufficientFundsException, ConcurrentBalanceChangeAttemptsException {
        IBalance balance = new Balance();
        balance.changeBalance(new MoneyAmount(Currency.getInstance("EUR"), 120), BalanceChangeType.DEPOSIT);
        balance.changeBalance(new MoneyAmount(Currency.getInstance("RSD"), 120), BalanceChangeType.DEPOSIT);
        
        String balanceString = balance.toString();
        
        assertFalse(balanceString.isEmpty());
    }
    
}

