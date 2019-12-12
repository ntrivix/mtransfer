package com.trivix.rest;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.trivix.mtransfer.common.valueobjects.MoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import com.trivix.mtransfer.domain.account.IAccount;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceCommand;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceCommandHandler;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceCommandResult;
import com.trivix.mtransfer.domain.account.commands.createAccount.CreateAccountCommand;
import com.trivix.mtransfer.domain.account.commands.createAccount.CreateAccountCommandHandler;
import com.trivix.mtransfer.domain.account.queries.AccountQuery;
import com.trivix.mtransfer.domain.account.queries.AccountQueryHandler;
import com.trivix.mtransfer.domain.account.queries.AccountView;
import com.trivix.mtransfer.domain.account.valueobjects.AccountIdentifier;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Currency;

import static spark.Spark.*;

public class AccountController {
    
    private Gson gson;
    private CreateAccountCommandHandler createAccountCommandHandler;
    private AccountQueryHandler accountQueryHandler;
    private ChangeAccountBalanceCommandHandler changeAccountBalanceCommandHandler;
    
    @Inject
    public AccountController(Gson gson, CreateAccountCommandHandler createAccountCommandHandler, AccountQueryHandler accountQueryHandler, ChangeAccountBalanceCommandHandler changeAccountBalanceCommandHandler) {
        this.gson = gson;
        this.createAccountCommandHandler = createAccountCommandHandler;
        this.accountQueryHandler = accountQueryHandler;
        this.changeAccountBalanceCommandHandler = changeAccountBalanceCommandHandler;
    }
    
    public void setPaths() {
        path("/account", () -> {
            put("/create", this::createAccount, gson::toJson);
            get("/:id", this::getAccount, gson::toJson);
            post("/:id/deposit/:currency/:amount", (rq, rs) -> changeBalance(rq, rs, AccountTransactionType.DEPOSIT), gson::toJson);
            post("/:id/withdraw/:currency/:amount", (rq, rs) -> changeBalance(rq, rs, AccountTransactionType.WITHDRAW), gson::toJson);
        });
    }

    private Object changeBalance(Request request, Response response, AccountTransactionType accountTransactionType) {
        String id = request.params(":id");
        String currency = request.params(":currency");
        String amount = request.params(":amount");
        try {
            ChangeAccountBalanceCommandResult result = changeAccountBalanceCommandHandler.executeCommand(new ChangeAccountBalanceCommand(
                    new AccountIdentifier(id),
                    new MoneyAmount(Currency.getInstance(currency), new BigDecimal(amount)),
                    accountTransactionType
            ));
            if (!result.isSuccessful())
                response.status(400);
            return result;
        } catch (NumberFormatException e) {
            response.status(400);
            return "Invalid number format";
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "Invalid currency code";
        }
    }

    private Object createAccount(Request request, Response response) {
        return createAccountCommandHandler.executeCommand(
            new CreateAccountCommand());
    }

    private Object getAccount(Request request, Response response) {
        try {
            IAccount account = accountQueryHandler.executeQuery(
                    new AccountQuery(new AccountIdentifier(
                            request.params(":id")
                    ))
            );
            if (account != null)
                return new AccountView(account);
            
            response.status(404);
            return "Account not found";
        } catch (IllegalArgumentException e) {
            response.status(400);
            response.body(e.getMessage());
            return e.getMessage();
        }
    }
}
