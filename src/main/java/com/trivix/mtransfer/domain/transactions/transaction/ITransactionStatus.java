package com.trivix.mtransfer.domain.transactions.transaction;

public interface ITransactionStatus {
    
    ITransactionInfo getTransactionInfo();
    
    TransactionState getPreviousState();
    TransactionState getTransactionState();
    String getStatusDescription();
    long getUnixTimestamp();
}
