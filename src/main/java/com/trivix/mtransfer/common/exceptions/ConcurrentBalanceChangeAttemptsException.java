package com.trivix.mtransfer.common.exceptions;

import com.trivix.mtransfer.domain.account.valueobjects.IBalance;
import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;

public class ConcurrentBalanceChangeAttemptsException extends AccountBalanceException {
    private IMoneyAmount amount;

    public ConcurrentBalanceChangeAttemptsException(IBalance balance, IMoneyAmount amount) {
        super("Can't perform balance change right now. To many concurrent attempts.", balance);
        this.amount = amount;
    }
}
