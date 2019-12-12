package com.trivix.mtransfer.domain.transactions.transaction;

import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;
import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;

import java.util.UUID;

public interface ITransactionInfo {
    UUID getTransactionId();
    IAccountIdentifier getSourceAccountId();
    IAccountIdentifier getTargetAccountId();
    IMoneyAmount getTransactionAmount();
    String getNote();
}
