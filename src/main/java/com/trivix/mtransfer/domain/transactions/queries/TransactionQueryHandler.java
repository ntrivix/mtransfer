package com.trivix.mtransfer.domain.transactions.queries;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trivix.common.core.events.IEvent;
import com.trivix.common.core.events.IEventStore;
import com.trivix.common.core.query.IQueryHandler;
import com.trivix.common.utils.collections.readonly.IReadOnlyCollection;
import com.trivix.common.utils.collections.readonly.ReadOnlyList;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionStatus;
import com.trivix.mtransfer.domain.transactions.transaction.ITransactionInfo;

import java.util.ArrayList;

@Singleton
public class TransactionQueryHandler implements IQueryHandler<TransactionQueryResult, TransactionQuery> {
    
    private IEventStore eventStore;

    @Inject
    public TransactionQueryHandler(IEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public TransactionQueryResult executeQuery(TransactionQuery query) {
        IReadOnlyCollection<IEvent> transactionEvents = eventStore.getEvents(query.getTransactionId());
        if (transactionEvents == null || transactionEvents.isEmpty())
            return null;

        ArrayList<TransactionStatus> transactionStatuses = new ArrayList<>();
        ITransactionInfo transactionInfo = null;
        for (IEvent event : transactionEvents) {
            if (event.getDataType() == TransactionStatus.class) {
                TransactionStatus transactionStatus = (TransactionStatus) event.getData();
                transactionStatuses.add(transactionStatus);
                transactionInfo = transactionStatus.getTransactionInfo();
            }
        }
        
        return new TransactionQueryResult(
                transactionInfo, 
                transactionStatuses);
    }
}
