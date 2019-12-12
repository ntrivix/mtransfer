package com.trivix.mtransfer.domain.account.commands.changeBalance;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trivix.common.core.command.ICommandHandler;
import com.trivix.common.core.events.AddEventStatus;
import com.trivix.common.core.events.Event;
import com.trivix.common.core.events.EventSubmissionException;
import com.trivix.common.core.events.IEventStore;
import com.trivix.common.utils.retry.Retry;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.events.BalanceChangedEventData;
import com.trivix.mtransfer.domain.account.valueobjects.IBalance;
import com.trivix.mtransfer.domain.account.queries.AccountQuery;
import com.trivix.mtransfer.domain.account.queries.AccountQueryHandler;

import java.util.Currency;

import static com.trivix.common.core.events.AddEventStatus.DUPLICATE_MESSAGE;

@Singleton
public class ChangeAccountBalanceCommandHandler implements ICommandHandler<ChangeAccountBalanceCommandResult, ChangeAccountBalanceCommand> {

    private IEventStore eventStore;
    private AccountQueryHandler accountQuery;
    
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_SLEEP_MS = 20;

    @Inject
    public ChangeAccountBalanceCommandHandler(
            IEventStore eventStore, 
            AccountQueryHandler accountQuery) {
        this.eventStore = eventStore;
        this.accountQuery = accountQuery;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangeAccountBalanceCommandResult executeCommand(ChangeAccountBalanceCommand command) {
        try {
            return Retry.execute(() -> 
                    changeBalance(command), 
                    MAX_RETRIES, 
                    RETRY_SLEEP_MS, 
                    EventSubmissionException.class);
        } catch (EventSubmissionException e) {
            ChangeAccountBalanceStatus status = ChangeAccountBalanceStatus.INCONSISTENT_STATE;
            if (e.getStatus() == DUPLICATE_MESSAGE)
                status = ChangeAccountBalanceStatus.DUPLICATE_TRANSACTION;
            
            return new ChangeAccountBalanceCommandResult(status, "Can't process transaction!");
        }
    }
    
    private ChangeAccountBalanceCommandResult changeBalance(ChangeAccountBalanceCommand command) {
        IAccount account = accountQuery.executeQuery(new AccountQuery(command.getAccountIdentifier()));
        if (account == null)
            return new ChangeAccountBalanceCommandResult(ChangeAccountBalanceStatus.INVALID_ACCOUNT, "Account with id " + command.getAccountIdentifier() + " does not exists!");

        if (!isEnoughFundsOnAccount(account.getBalance(), command))
            return new ChangeAccountBalanceCommandResult(ChangeAccountBalanceStatus.INSUFFICIENT_MONEY, "Insufficient funds. Can't perform operation.");

        Event<BalanceChangedEventData> balanceChangedEvent = constructEvent(account, command);
        AddEventStatus eventStatus = eventStore.addEvent(balanceChangedEvent);

        if (eventStatus != AddEventStatus.SUCCESS)
            throw new EventSubmissionException(balanceChangedEvent, eventStatus);

        return new ChangeAccountBalanceCommandResult(ChangeAccountBalanceStatus.SUCCESSFUL);
    }
    
    private Event<BalanceChangedEventData> constructEvent(IAccount account, ChangeAccountBalanceCommand command) {
        return Event.create(
                new BalanceChangedEventData(
                        account,
                        command.getMoneyAmount(),
                        command.getTransactionType(),
                        command.getDescription()
                ),
                account.getAccountId().getUUID(),
                command.getTransactionId(),
                account.getVersion() + 1
        );
    }
    
    private boolean isEnoughFundsOnAccount(IBalance balance, ChangeAccountBalanceCommand command) {
        if (command.getTransactionType() == AccountTransactionType.DEPOSIT)
            return true;
        
        Currency currency = command.getMoneyAmount().getCurrency();
        return balance.getMoneyValue(currency).compareTo(command.getMoneyAmount()) >= 0;
    }
}
