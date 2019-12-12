package com.trivix.mtransfer.domain.transactions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trivix.common.core.events.AddEventStatus;
import com.trivix.common.core.events.Event;
import com.trivix.common.core.events.IEvent;
import com.trivix.common.core.events.IEventStore;
import com.trivix.common.utils.collections.readonly.IReadOnlyCollection;
import com.trivix.mtransfer.domain.account.BalanceChangeType;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceCommand;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceCommandHandler;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceCommandResult;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceStatus;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;
import com.trivix.mtransfer.domain.transactions.transaction.ITransactionInfo;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionState;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionStatus;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class TransactionsProcessorSaga {
    private IEventStore eventStore;
    private ChangeAccountBalanceCommandHandler changeAccountBalanceCommandHandler;
    private ExecutorService transactionEventsExecutor;

    @Inject
    public TransactionsProcessorSaga(IEventStore eventStore, ChangeAccountBalanceCommandHandler changeAccountBalanceCommandHandler) {
        this.eventStore = eventStore;
        this.changeAccountBalanceCommandHandler = changeAccountBalanceCommandHandler;
        this.transactionEventsExecutor = Executors.newCachedThreadPool();
        this.eventStore.registerEventHandler(TransactionStatus.class, this::enqueueEventForProcessing);
    }

    private void enqueueEventForProcessing(IEvent<TransactionStatus> event) {
        transactionEventsExecutor.execute(() -> processTransactionEvent(event));
    }

    private void processTransactionEvent(IEvent<TransactionStatus> event)
    {   
        TransactionState transactionState = event.getData().getTransactionState();
        if (transactionState == TransactionState.PENDING)
            processPendingState(event);
        else if (transactionState == TransactionState.MONEY_WITHDRAWN)
            processWithdrawnState(event);
    }
    
    private void processPendingState(IEvent<TransactionStatus> event)
    {
        ITransactionInfo transactionInfo = event.getData().getTransactionInfo();
        IAccountIdentifier sourceAccountId = transactionInfo.getSourceAccountId();
        ChangeAccountBalanceCommandResult result = changeAccountBalanceCommandHandler.executeCommand(new ChangeAccountBalanceCommand(
                sourceAccountId, transactionInfo.getTransactionAmount(), BalanceChangeType.WITHDRAW, transactionInfo.getTransactionId()
        )); 
        
        if (result.isSuccessful())
            addEvent(event, TransactionState.PENDING, TransactionState.MONEY_WITHDRAWN, event.getVersion() + 1,  "Money withdrawn.");
        else if (result.getStatus() == ChangeAccountBalanceStatus.INSUFFICIENT_MONEY ||
                result.getStatus() == ChangeAccountBalanceStatus.INVALID_ACCOUNT ||
                result.getStatus() == ChangeAccountBalanceStatus.INCONSISTENT_STATE)
            abortTransaction(transactionInfo.getTransactionId());
    }
    
    private void processWithdrawnState(IEvent<TransactionStatus> event) {
        ITransactionInfo transactionInfo = event.getData().getTransactionInfo();
        IAccountIdentifier sourceAccountId = transactionInfo.getTargetAccountId();
        ChangeAccountBalanceCommandResult result = changeAccountBalanceCommandHandler.executeCommand(new ChangeAccountBalanceCommand(
                sourceAccountId, transactionInfo.getTransactionAmount(), BalanceChangeType.DEPOSIT, transactionInfo.getTransactionId()
        ));

        if (result.isSuccessful())
            addEvent(event, TransactionState.MONEY_WITHDRAWN, TransactionState.APPROVED, event.getVersion() + 1,  "Money deposited.");
        else if (result.getStatus() == ChangeAccountBalanceStatus.INSUFFICIENT_MONEY ||
                result.getStatus() == ChangeAccountBalanceStatus.INVALID_ACCOUNT ||
                result.getStatus() == ChangeAccountBalanceStatus.INCONSISTENT_STATE)
            abortTransaction(transactionInfo.getTransactionId());
    }
    
    private void abortTransaction(UUID transactionId) {
        UUID rollbackTransactionId = UUID.randomUUID();
        
        IReadOnlyCollection<IEvent> events = eventStore.getEventsAfterVersion(transactionId, 0);
        boolean moneyWithdrawn = false;
        ITransactionInfo transactionInfo = null;
        int lastEventVersion = -1;
        TransactionState lastEventState = TransactionState.UNINITIALIZED;
        for (IEvent event : events) {
            if (event.getDataType() != TransactionStatus.class)
                continue;
            TransactionStatus status = (TransactionStatus) event.getData();
            if (status.getTransactionState() == TransactionState.MONEY_WITHDRAWN)
                moneyWithdrawn = true;
            else if (status.getTransactionState() == TransactionState.APPROVED)
                return;
            transactionInfo = status.getTransactionInfo();
            lastEventVersion = event.getVersion();
            lastEventState = status.getTransactionState();
        }
        
        if (lastEventVersion == -1)
            return;
        
        if (moneyWithdrawn)
            changeAccountBalanceCommandHandler.executeCommand(new ChangeAccountBalanceCommand(
                    transactionInfo.getSourceAccountId(),
                    transactionInfo.getTransactionAmount(),
                    BalanceChangeType.DEPOSIT,
                    rollbackTransactionId
            ));
            
        addEvent(Event.create(new TransactionStatus(
                transactionInfo, lastEventState, TransactionState.ABORTED, "Transaction aborted"
        ), transactionId, rollbackTransactionId, lastEventVersion + 1), lastEventState, TransactionState.ABORTED, lastEventVersion + 1, "Transaction aborted");
    }
    
    private AddEventStatus addEvent(IEvent<TransactionStatus> event, TransactionState currentState, TransactionState newState, int version, String description) {
        ITransactionInfo transactionInfo = event.getData().getTransactionInfo();
        return eventStore.addEvent(Event.create(
                new TransactionStatus(
                        transactionInfo,
                        currentState,
                        newState,
                        description),
                transactionInfo.getTransactionId(),
                UUID.randomUUID(),
                version));
    }
}
