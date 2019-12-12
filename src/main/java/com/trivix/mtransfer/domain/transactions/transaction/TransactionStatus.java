package com.trivix.mtransfer.domain.transactions.transaction;

import com.trivix.mtransfer.domain.transactions.transaction.ITransactionInfo;
import com.trivix.mtransfer.domain.transactions.transaction.ITransactionStatus;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionState;

public class TransactionStatus implements ITransactionStatus {
    
    private ITransactionInfo transactionInfo;
    private TransactionState previousState;
    private TransactionState state;
    
    private String statusDescription;
    private long unixTime;

    public TransactionStatus(
            ITransactionInfo transactionInfo,
            TransactionState previousState,
            TransactionState state,
            String statusDescription) {
        this.transactionInfo = transactionInfo;
        this.previousState = previousState;
        this.state = state;
        this.statusDescription = statusDescription;
        unixTime = System.currentTimeMillis();
    }

    @Override
    public ITransactionInfo getTransactionInfo() {
        return this.transactionInfo;
    }

    @Override
    public TransactionState getPreviousState() {
        return previousState;
    }

    @Override
    public TransactionState getTransactionState() {
        return state;
    }

    @Override
    public String getStatusDescription() {
        return statusDescription;
    }

    @Override
    public long getUnixTimestamp() {
        return unixTime;
    }

}
