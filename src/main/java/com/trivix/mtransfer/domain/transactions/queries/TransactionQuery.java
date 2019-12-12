package com.trivix.mtransfer.domain.transactions.queries;

import com.trivix.common.core.query.IQuery;

import java.util.UUID;

public class TransactionQuery implements IQuery<TransactionQueryResult> {
    private UUID transactionId;

    public TransactionQuery(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
}
