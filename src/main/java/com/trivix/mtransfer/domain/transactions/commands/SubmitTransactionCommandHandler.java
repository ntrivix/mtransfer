package com.trivix.mtransfer.domain.transactions.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trivix.common.core.command.ICommandHandler;
import com.trivix.common.core.events.AddEventStatus;
import com.trivix.common.core.events.Event;
import com.trivix.common.core.events.IEventStore;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.queries.AccountQuery;
import com.trivix.mtransfer.domain.account.queries.AccountQueryHandler;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionStatus;
import com.trivix.mtransfer.domain.transactions.queries.TransactionQuery;
import com.trivix.mtransfer.domain.transactions.queries.TransactionQueryHandler;
import com.trivix.mtransfer.domain.transactions.queries.TransactionQueryResult;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionState;

@Singleton
public class SubmitTransactionCommandHandler implements ICommandHandler<TransactionStatus, SubmitTransactionCommand> {

    private AccountQueryHandler accountQuery;
    private TransactionQueryHandler transactionQuery;
    private IEventStore eventStore;

    @Inject
    public SubmitTransactionCommandHandler(AccountQueryHandler accountQuery, TransactionQueryHandler transactionQuery, IEventStore eventStore) {
        this.accountQuery = accountQuery;
        this.transactionQuery = transactionQuery;
        this.eventStore = eventStore;
    }

    @Override
    public TransactionStatus executeCommand(SubmitTransactionCommand command) {
        TransactionQueryResult transaction = transactionQuery.executeQuery(new TransactionQuery(command.getTransactionId()));
        if (transaction != null)
            return transaction.getTransactionDetails().get(
                    transaction.getTransactionDetails().size()
            );

        TransactionStatus invalidAccountStatus = getInvalidAccountStatus(command);
        if (invalidAccountStatus != null)
            return invalidAccountStatus;
        
        TransactionStatus transactionInitializedStatus = new TransactionStatus(
                command.getTransactionInfo(),
                TransactionState.UNINITIALIZED,
                TransactionState.PENDING,
                "Transaction initialized"
        );
        
        AddEventStatus status = eventStore.addEvent(Event.create(
                transactionInitializedStatus, 
                command.getTransactionId(), 
                command.getTransactionId(), 
                0));
        
        if (status != AddEventStatus.SUCCESS)
            return new TransactionStatus(
                    command.getTransactionInfo(),
                    TransactionState.UNINITIALIZED, 
                    TransactionState.ABORTED, 
                    "Invalid transaction. Please try again.");
        
        return transactionInitializedStatus;
    }
    
    private TransactionStatus getInvalidAccountStatus(SubmitTransactionCommand command) {
        IAccount from = accountQuery.executeQuery(new AccountQuery(command.getFrom()));
        if (from == null)
            return new TransactionStatus(
                    command.getTransactionInfo(),
                    TransactionState.UNINITIALIZED,
                    TransactionState.ABORTED,
                    "Account does not exist " + command.getFrom()
            );

        IAccount to = accountQuery.executeQuery(new AccountQuery(command.getTo()));
        if (to == null)
            return new TransactionStatus(
                    command.getTransactionInfo(),
                    TransactionState.UNINITIALIZED,
                    TransactionState.ABORTED,
                    "Account does not exist " + command.getTo()
            );
        
        return null;
    }
}
