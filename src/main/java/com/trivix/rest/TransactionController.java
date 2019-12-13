package com.trivix.rest;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.trivix.mtransfer.common.valueobjects.MoneyAmount;
import com.trivix.mtransfer.domain.account.valueobjects.AccountIdentifier;
import com.trivix.mtransfer.domain.transactions.commands.SubmitTransactionCommand;
import com.trivix.mtransfer.domain.transactions.commands.SubmitTransactionCommandHandler;
import com.trivix.mtransfer.domain.transactions.queries.TransactionQuery;
import com.trivix.mtransfer.domain.transactions.queries.TransactionQueryHandler;
import com.trivix.mtransfer.domain.transactions.queries.TransactionQueryResult;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static spark.Spark.*;

public class TransactionController {
    private Gson gson;
    private SubmitTransactionCommandHandler submitTransactionCommandHandler;
    private TransactionQueryHandler transactionQueryHandler;

    @Inject
    public TransactionController(Gson gson, SubmitTransactionCommandHandler submitTransactionCommandHandler, TransactionQueryHandler transactionQueryHandler) {
        this.gson = gson;
        this.submitTransactionCommandHandler = submitTransactionCommandHandler;
        this.transactionQueryHandler = transactionQueryHandler;
    }

    public void setPaths() {
        path("/transaction", () -> {
            post("", this::submitTransaction, gson::toJson);
            get("/:id", this::getTransactionStatus, gson::toJson);
        });
    }

    private Object getTransactionStatus(Request request, Response response) {
        String transactionId = request.params(":id");
        try {
            TransactionQueryResult transaction = transactionQueryHandler.executeQuery(
                    new TransactionQuery(UUID.fromString(transactionId)));
                    
            if (transaction == null) {
                response.status(400);
                return "Transaction does not exist";
            }
            
            return transaction;
        } catch (IllegalArgumentException e) {
            response.status(400);
            return e.getMessage();
        }
    }

    private Object submitTransaction(Request request, Response response) {
        try {
            SubmitTransactionCommand command = createTransactionCommand(request);
            return submitTransactionCommandHandler.executeCommand(command);
        } catch (NumberFormatException e) {
            response.status(400);
            return "Amount format is invalid";
        } catch (IllegalArgumentException e) {
            response.status(400);
            if (e.getMessage() == null || e.getMessage().isEmpty())
                return "One or more invalid parameters";
            return e.getMessage();
        }
    }
    
    private SubmitTransactionCommand createTransactionCommand(Request request) {
        String from = request.queryParamOrDefault("from", null);
        String to = request.queryParamOrDefault("to", null);
        String amount = request.queryParamOrDefault("amount", null);
        String currency = request.queryParamOrDefault("currency", null);
        String note = request.queryParamOrDefault("note", "");

        if (from == null || to == null || amount == null || currency == null)
            throw new IllegalArgumentException("All parameters must be provided [from, to, amount, currency]");

        return new SubmitTransactionCommand(
                UUID.randomUUID(),
                new AccountIdentifier(from),
                new AccountIdentifier(to),
                new MoneyAmount(Currency.getInstance(currency), new BigDecimal(amount)),
                note
        );
    }


}
