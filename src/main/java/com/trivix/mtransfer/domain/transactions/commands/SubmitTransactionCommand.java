package com.trivix.mtransfer.domain.transactions.commands;

import com.trivix.common.core.command.ICommand;
import com.trivix.mtransfer.common.valueobjects.contracts.IMoneyAmount;
import com.trivix.mtransfer.domain.account.valueobjects.IAccountIdentifier;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionInfo;
import com.trivix.mtransfer.domain.transactions.transaction.TransactionStatus;
import com.trivix.mtransfer.domain.transactions.transaction.ITransactionInfo;

import java.util.UUID;

public class SubmitTransactionCommand implements ICommand<TransactionStatus> {
    
    private ITransactionInfo transactionInfo;
    private String note;

    public SubmitTransactionCommand(UUID transactionId, IAccountIdentifier from, IAccountIdentifier to, IMoneyAmount amount, String note) {
        transactionInfo = new TransactionInfo(transactionId, from, to, amount, note);
        this.note = note;
    }

    public UUID getTransactionId() {
        return transactionInfo.getTransactionId();
    }

    public IAccountIdentifier getFrom() {
        return transactionInfo.getSourceAccountId();
    }

    public IAccountIdentifier getTo() {
        return transactionInfo.getTargetAccountId();
    }

    public IMoneyAmount getAmount() {
        return transactionInfo.getTransactionAmount();
    }

    public ITransactionInfo getTransactionInfo() {
        return transactionInfo;
    }
}
