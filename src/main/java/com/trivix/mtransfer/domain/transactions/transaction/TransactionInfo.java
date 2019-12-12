package com.trivix.mtransfer.domain.transactions.transaction;

import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionInfo implements ITransactionInfo {
    
    private UUID transactionId;
    private IAccountIdentifier sourceAccountId;
    private IAccountIdentifier targetAccountId;
    private IMoneyAmount moneyAmount;
    private String note;

    public TransactionInfo(UUID transactionId, IAccountIdentifier sourceAccountId, IAccountIdentifier targetAccountId, IMoneyAmount moneyAmount, String note) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        
        if (sourceAccountId == targetAccountId)
            throw new IllegalArgumentException("Source and Target account must be different");
        
        this.note = note;
        if (moneyAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount can't be negative!");
        this.moneyAmount = moneyAmount;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public IAccountIdentifier getSourceAccountId() {
        return sourceAccountId;
    }

    public IAccountIdentifier getTargetAccountId() {
        return targetAccountId;
    }

    public IMoneyAmount getTransactionAmount() {
        return moneyAmount;
    }

    @Override
    public String getNote() {
        return note;
    }
}
