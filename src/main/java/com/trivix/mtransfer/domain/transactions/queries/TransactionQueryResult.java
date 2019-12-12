package com.trivix.mtransfer.domain.transactions.queries;

import com.trivix.common.utils.collections.readonly.IReadOnlyCollection;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionStatus;
import com.trivix.mtransfer.domain.transactions.transaction.ITransactionInfo;

import java.util.ArrayList;

public class TransactionQueryResult {
    private ITransactionInfo transaction;
    private ArrayList<TransactionStatus> transactionDetails;

    public TransactionQueryResult(ITransactionInfo transaction, ArrayList<TransactionStatus> transactionDetails) {
        this.transaction = transaction;
        this.transactionDetails = transactionDetails;
    }

    public ITransactionInfo getTransaction() {
        return transaction;
    }

    public ArrayList<TransactionStatus> getTransactionDetails() {
        return transactionDetails;
    }
}
