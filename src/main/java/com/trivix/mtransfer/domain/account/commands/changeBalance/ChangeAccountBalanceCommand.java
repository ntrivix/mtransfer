package com.trivix.mtransfer.domain.account.commands.changeBalance;

import com.trivix.common.core.command.ICommand;
import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Change account balance for the given amount.
 * Amount must be positive.
 * To withdraw money transactionType must be {@link AccountTransactionType}.WITHDRAW
 */
public class ChangeAccountBalanceCommand implements ICommand<ChangeAccountBalanceCommandResult> {

    private IAccountIdentifier accountIdentifier;
    private IMoneyAmount moneyAmount;
    private AccountTransactionType transactionType;
    private String description;

    /**
     * Unique command identifier to ensure that command is executed only once.
     */
    private UUID transactionId;

    public ChangeAccountBalanceCommand(IAccountIdentifier accountIdentifier, IMoneyAmount moneyAmount, AccountTransactionType transactionType) {
        this.accountIdentifier = accountIdentifier;
        if (moneyAmount == null)
            throw new IllegalArgumentException("Amount not defined");
        if (moneyAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be positive");
        this.moneyAmount = moneyAmount;
        this.transactionType = transactionType;
        this.transactionId = UUID.randomUUID();
    }

    public ChangeAccountBalanceCommand(IAccountIdentifier accountIdentifier, IMoneyAmount moneyAmount, AccountTransactionType transactionType, UUID referenceTransactionId) {
        this(accountIdentifier, moneyAmount, transactionType, "Transaction " + referenceTransactionId.toString());
        this.transactionId = referenceTransactionId;
    }

    public ChangeAccountBalanceCommand(IAccountIdentifier accountIdentifier, IMoneyAmount moneyAmount, AccountTransactionType transactionType, String description) {
        this(accountIdentifier, moneyAmount, transactionType);
        this.description = description;
    }

    public IAccountIdentifier getAccountIdentifier() {
        return accountIdentifier;
    }

    public IMoneyAmount getMoneyAmount() {
        return moneyAmount;
    }

    public AccountTransactionType getTransactionType() {
        return transactionType;
    }

    public String getDescription() {
        return description;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
}
