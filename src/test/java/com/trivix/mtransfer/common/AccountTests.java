package com.trivix.mtransfer.common;

import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;
import com.trivix.mtransfer.domain.account.AccountAggregate;
import com.trivix.mtransfer.domain.account.IAccount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTests {
    
    @Test
    void TwoAccountEqualityTest() {
        AccountAggregate account1 = new AccountAggregate();
        AccountAggregate account2 = new AccountAggregate();
        
        assertEquals(account1, account1);
        assertNotEquals(account1, account2);
        assertNotEquals(account1.hashCode(), account2.hashCode());
    }
    
    @Test
    void TwoAccountsHaveDifferentIdTest()
    {
        IAccount account1 = new AccountAggregate();
        IAccount account2 = new AccountAggregate();
        
        assertNotEquals(account1.getAccountId(), account2.getAccountId());
    }
    
    @Test
    void CreateAccountBalanceNotNullTest()
    {
        IAccount account = new AccountAggregate();
        
        assertNotNull(account.getBalance());
    }
    
    @Test
    void CreateAccountZeroBalanceTest() {
        IAccount account = new AccountAggregate();
        IMoneyAmount moneyValue = account.getBalance()
                .getMoneyValue(Currency.getInstance(Locale.UK));

        assertEquals(BigDecimal.ZERO, moneyValue.getValue());
    }
}
