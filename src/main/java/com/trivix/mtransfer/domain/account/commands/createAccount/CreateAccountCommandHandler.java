package com.trivix.mtransfer.domain.account.commands.createAccount;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trivix.common.core.command.ICommandHandler;
import com.trivix.common.core.events.AddEventStatus;
import com.trivix.common.core.events.Event;
import com.trivix.common.core.events.IEventStore;
import com.trivix.mtransfer.domain.account.events.AccountCreatedEventData;
import com.trivix.mtransfer.domain.account.valueobjects.AccountIdentifier;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

@Singleton
public class CreateAccountCommandHandler implements ICommandHandler<CreateAccountCommandResult, CreateAccountCommand> {
    
    private IEventStore eventStore;

    @Inject
    public CreateAccountCommandHandler(IEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public CreateAccountCommandResult executeCommand(CreateAccountCommand inputType) {
        IAccountIdentifier identifier = new AccountIdentifier();
        AddEventStatus status = eventStore.addEvent(Event.create(
                new AccountCreatedEventData(identifier),
                identifier.getUUID(),
                identifier.getUUID(),
                0
        ));
        if (status != AddEventStatus.SUCCESS)
            return new CreateAccountCommandResult(null);
        
        return new CreateAccountCommandResult(identifier);
    }
}
