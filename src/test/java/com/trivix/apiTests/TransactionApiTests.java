package com.trivix.apiTests;

import com.trivix.App;
import com.trivix.mtransfer.common.valueobjects.IMoneyAmount;
import com.trivix.mtransfer.common.valueobjects.MoneyAmount;
import com.trivix.mtransfer.domain.account.AccountTransactionType;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionApiTests {

    private App app;

    @BeforeAll
    public void prepareServer() {
        app = new App();
        app.run();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.getPort();
        RestAssured.basePath = "/api";
    }

    @AfterAll
    public void stopServer() {
        app.stopServer();
    }

    @Test
    public void makeTransactionStatusPendingTest() {
        String account1 = createAccount();
        String account2 = createAccount();
        
        changeBalance(account1, new MoneyAmount(Currency.getInstance("EUR"), 100), AccountTransactionType.DEPOSIT);

        makeTransaction(account1, account2, "99.99", "EUR").
                then().
                assertThat().
                body("state", is("PENDING")).
                statusCode(200);
    }

    @DisplayName("Invalid transaction")
    @ParameterizedTest(name = "run #{index} with [{arguments}]")
    @CsvSource({ 
            "132, 123, 1, EUR", // Invalid account ids 
            "e9666fcc-a545-4a4d-9935-e86c4951a00a, d4a8d050-5c24-4352-afcf-adb3a7106fa6, -22, EUR", // Negative moneyAmount
            "e9666fcc-a545-4a4d-9935-e86c4951a00a, d4a8d050-5c24-4352-afcf-adb3a7106fa6, 100, 231" // Invalid currency
    }) 
    public void makeInvalidTransactionTest(String from, String to, String amount, String currency) {
        makeTransaction(from, to, amount, currency).
                then().
                assertThat().
                statusCode(400);
    }

    @Test
    public void makeTransactionInsufficientMoneyTest() throws InterruptedException {
        String account1 = createAccount();
        String account2 = createAccount();
        
        String transactionId = makeTransaction(account1, account2, "99.99", "EUR").
                path("transactionInfo.transactionId");
        
        Thread.sleep(10);
        
        get("/transaction/" + transactionId).
                then().
                assertThat().
                body("transactionDetails[-1].state", is("ABORTED"));
    }

    @DisplayName("Valid transaction")
    @ParameterizedTest(name = "run #{index} with [{arguments}]")
    @CsvSource({
            "1, 1, EUR",
            "0.1, 0.1, RSD",
            "500, 0.1, RSD",
            "500, 250, EUR",
    })
    public void transactionAssureMoneyTransferred(BigDecimal deposit, String transfer, String currency) throws InterruptedException {
        String account1 = createAccount();
        String account2 = createAccount();

        changeBalance(account1, new MoneyAmount(Currency.getInstance(currency), deposit), AccountTransactionType.DEPOSIT);

        String transactionId = makeTransaction(account1, account2, transfer, currency).
                path("transactionInfo.transactionId");
        
        Thread.sleep(10);

        get("/transaction/" + transactionId).
                then().
                assertThat().
                body("transactionDetails[-1].state", is("APPROVED"));
        
        assertAccountBalance(account1, deposit.subtract(new BigDecimal(transfer)).toString(), currency);
        assertAccountBalance(account2, transfer, currency);
    }
    
    private void assertAccountBalance(String accountId, String amount, String currency) {
        String accountAmount = given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                when().
                get("/account/" + accountId).
                then().
                extract().
                path("moneyAmounts." + currency).
                toString();
        assertTrue(new BigDecimal(accountAmount).compareTo(new BigDecimal(amount)) == 0);
    }
    
    private Response makeTransaction(String from, String to, String amount, String currency)
    {
        return given().
                queryParam("from", from).
                queryParam("to", to).
                queryParam("amount", amount).
                queryParam("currency", currency).
                when().
                post("/transaction");
    }

    /**
     * Create account and return account id.
      * @return
     */ 
    private String createAccount() {
        return put("/account/create").path("newAccountIdentifier.accountId");
    }
    
    private void changeBalance(String id, IMoneyAmount moneyAmount, AccountTransactionType transactionType)
    {
        post("/account/" + id + "/"+ transactionType.toString().toLowerCase() +"/" + moneyAmount.getCurrency().getCurrencyCode()  + "/" + moneyAmount.getValue()).then().
                assertThat().
                statusCode(200).
                body("status", equalTo("SUCCESSFUL"));
    }
}
