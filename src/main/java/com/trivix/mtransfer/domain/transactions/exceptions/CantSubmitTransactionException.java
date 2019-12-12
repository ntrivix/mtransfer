package com.trivix.mtransfer.domain.transactions.exceptions;

import com.trivix.mtransfer.domain.transactions.transaction.ITransactionInfo;

public class CantSubmitTransactionException extends Exception {
    private ITransactionInfo transaction;

    public CantSubmitTransactionException(ITransactionInfo transaction, String message, Throwable cause) {
        super(message, cause);
        this.transaction = transaction;
    }
}
