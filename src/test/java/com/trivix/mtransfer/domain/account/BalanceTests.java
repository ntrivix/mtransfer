package com.trivix.mtransfer.domain.account;

import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.common.valueobjects.MoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.common.exceptions.ConcurrentBalanceChangeAttemptsException;
import com.trivix.mtransfer.domain.account.valueobjects.IBalance;
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
        assertThrows(IllegalArgumentException.class, () -> balance.changeBalance(addAmount, AccountTransactionType.DEPOSIT));
    }
    
    @Test
    void creditBalanceTest() throws ConcurrentBalanceChangeAttemptsException {
        // Arrange.
        IBalance balance = new Balance();
        Currency currency = Currency.getInstance("EUR");
        IMoneyAmount addAmount = new MoneyAmount(currency, new BigDecimal(100));
        
        // Act.
        IMoneyAmount newAmount = balance.changeBalance(addAmount, AccountTransactionType.DEPOSIT);
        
        // Assert.
        assertEquals(balance.getMoneyValue(currency), newAmount);
        assertEquals(0, newAmount.compareTo(new BigDecimal(100)));
    }

    @Test
    void debitBalanceTest() throws ConcurrentBalanceChangeAttemptsException {
        // Arrange.
        IBalance balance = new Balance();
        Currency currency = Currency.getInstance("EUR");
        IMoneyAmount addAmount = new MoneyAmount(currency, new BigDecimal(100));
        
        balance.changeBalance(addAmount, AccountTransactionType.DEPOSIT);
        
        // Act.
        IMoneyAmount newAmount = balance.changeBalance(addAmount, AccountTransactionType.WITHDRAW);

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
            balance.changeBalance(initialAmount, AccountTransactionType.DEPOSIT);
        } catch (Exception e) {
            fail();
        }

        IMoneyAmount withdrawAmount = new MoneyAmount(currency, new BigDecimal(100.001f));
        IMoneyAmount moneyAmount = balance.changeBalance(
                withdrawAmount, 
                AccountTransactionType.WITHDRAW);

        // Assert.
        IMoneyAmount expectedAmount = initialAmount.subtract(withdrawAmount.getValue());
        assertEquals(0, moneyAmount.compareTo(expectedAmount));
        assertEquals(0, balance.getMoneyValue(currency).compareTo(expectedAmount));
    }
    
    @Test
    void toStringTest() throws ConcurrentBalanceChangeAttemptsException {
        IBalance balance = new Balance();
        balance.changeBalance(new MoneyAmount(Currency.getInstance("EUR"), 120), AccountTransactionType.DEPOSIT);
        balance.changeBalance(new MoneyAmount(Currency.getInstance("RSD"), 120), AccountTransactionType.DEPOSIT);
        
        String balanceString = balance.toString();
        
        assertFalse(balanceString.isEmpty());
    }
    
}

