package com.trivix.mtransfer.common.exceptions;

import com.trivix.mtransfer.domain.account.valueobjects.IBalance;
import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;

public class InsufficientFundsException extends AccountBalanceException {
    private IMoneyAmount requiredFunds;

    public InsufficientFundsException(IBalance balance, IMoneyAmount requiredFunds) {
        super("Operation can't be performed due to lack of funds on the account.", balance);
        this.requiredFunds = requiredFunds;
    }
}
