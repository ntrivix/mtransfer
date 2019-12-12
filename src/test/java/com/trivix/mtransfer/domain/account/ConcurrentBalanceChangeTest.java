package com.trivix.mtransfer.domain.account;

import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.common.valueobjects.MoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.domain.account.valueobjects.IBalance;
import com.trivix.mtransfer.domain.account.valueobjects.Balance;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import testutils.ConcurrentAssertion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ConcurrentBalanceChangeTest
{
    private IBalance balance;
    private ConcurrentLinkedQueue<Pair<IMoneyAmount, AccountTransactionType>> balanceChangeQueue = new ConcurrentLinkedQueue<>();

    private Currency eur = Currency.getInstance("EUR");
    private Currency gbp = Currency.getInstance("GBP");
    
    private IMoneyAmount initialEur;
    private IMoneyAmount initialGbp;

    
    public ConcurrentBalanceChangeTest() {
        balance = new Balance();

        try {
            initialEur = balance.changeBalance(
                    new MoneyAmount(eur, new BigDecimal(100)),
                    AccountTransactionType.DEPOSIT);
            initialGbp = balance.changeBalance(
                    new MoneyAmount(gbp, new BigDecimal(200)),
                    AccountTransactionType.DEPOSIT);
        } catch (Exception ignored) {}

        balanceChangeQueue = new ConcurrentLinkedQueue<>();

        balanceChangeQueue.add(new Pair<>(new MoneyAmount(eur, 30), AccountTransactionType.DEPOSIT));
        balanceChangeQueue.add(new Pair<>(new MoneyAmount(eur, 10), AccountTransactionType.WITHDRAW));
        balanceChangeQueue.add(new Pair<>(new MoneyAmount(eur, 20), AccountTransactionType.WITHDRAW));

        balanceChangeQueue.add(new Pair<>(new MoneyAmount(gbp, 30), AccountTransactionType.DEPOSIT));
        balanceChangeQueue.add(new Pair<>(new MoneyAmount(gbp, 10), AccountTransactionType.WITHDRAW));
        balanceChangeQueue.add(new Pair<>(new MoneyAmount(gbp, 20), AccountTransactionType.WITHDRAW));
    }

    @Test
    void balanceChange() throws InterruptedException {
        ArrayList<Runnable> runnable = new ArrayList<>();
        while (!balanceChangeQueue.isEmpty()) {
            Pair<IMoneyAmount, AccountTransactionType> balanceChangeRequest = balanceChangeQueue.poll();
            runnable.add(() -> {
                try {
                    balance.changeBalance(balanceChangeRequest.getKey(), balanceChangeRequest.getValue());
                } catch (Exception e) {
                    fail();
                } 
            });
        }

        ConcurrentAssertion.assertConcurrent("", runnable, 1);
        assertEquals(0, balance.getMoneyValue(eur).compareTo(initialEur));
        assertEquals(0, balance.getMoneyValue(gbp).compareTo(initialGbp));
    }
}
