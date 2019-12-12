package com.trivix.mtransfer.domain.account.commands.changeBalance;

import com.trivix.common.core.command.ICommand;
import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;
import com.trivix.mtransfer.domain.account.BalanceChangeType;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

public class ChangeAccountBalanceCommand implements ICommand<ChangeAccountBalanceCommandResult> {
    private IAccountIdentifier accountIdentifier;
    private IMoneyAmount moneyAmount;
    private BalanceChangeType changeType;
    private String description;
    private UUID transactionId;

    public ChangeAccountBalanceCommand(IAccountIdentifier accountIdentifier, IMoneyAmount moneyAmount, BalanceChangeType changeType) {
        this.accountIdentifier = accountIdentifier;
        if (moneyAmount == null)
            throw new IllegalArgumentException("Amount not defined");
        if (moneyAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be positive");
        this.moneyAmount = moneyAmount;
        this.changeType = changeType;
        this.transactionId = UUID.randomUUID();
    }

    public ChangeAccountBalanceCommand(IAccountIdentifier accountIdentifier, IMoneyAmount moneyAmount, BalanceChangeType changeType, UUID referenceTransactionId) {
        this(accountIdentifier, moneyAmount, changeType, "Transaction " + referenceTransactionId.toString());
        this.transactionId = referenceTransactionId;
    }

    public ChangeAccountBalanceCommand(IAccountIdentifier accountIdentifier, IMoneyAmount moneyAmount, BalanceChangeType changeType, String description) {
        this(accountIdentifier, moneyAmount, changeType);
        this.description = description;
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }

    public IMoneyAmount getMoneyAmount() {
        return moneyAmount;
    }

    public BalanceChangeType getChangeType() {
        return changeType;
    }

    public String getDescription() {
        return description;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
}
