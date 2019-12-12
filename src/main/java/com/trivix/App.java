package com.trivix;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.trivix.mtransfer.domain.account.commands.changeBalance.ChangeAccountBalanceCommandHandler;
import com.trivix.mtransfer.domain.account.exceptions.DuplicateAccountIdException;
import com.trivix.mtransfer.domain.transactions.TransactionsProcessorSaga;
import com.trivix.rest.AccountController;
import com.trivix.rest.TransactionController;

import java.math.BigDecimal;

import static spark.Spark.*;


public class App {
    
    public static void main(String[] args) throws DuplicateAccountIdException {
        new App();
    }

    public App() {
        
        Injector injector = Guice.createInjector(new MoneyTransferModule());

        injector.getProvider(ChangeAccountBalanceCommandHandler.class).get();
        injector.getProvider(TransactionsProcessorSaga.class).get();

        AccountController accountController = injector.getInstance(AccountController.class);
        TransactionController transactionController = injector.getInstance(TransactionController.class);

        path("/api", () -> {
            exception(Exception.class, (e, request, response) -> {
                response.body(e.toString());
            });
            before("/*",(request, response) -> {
                response.type("application/json");
            });

            transactionController.setPaths();
            accountController.setPaths();
            
        });
    }
}
