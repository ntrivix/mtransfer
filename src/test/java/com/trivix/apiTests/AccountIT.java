package com.trivix.apiTests;

import com.trivix.App;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

public class AccountIT {
    
    @BeforeAll
    public static void prepareServer() {
        App app = AppSingleton.getApp();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = app.getPort();
        RestAssured.basePath = "/api";
    }
    
    @Test
    public void createAccountTest() {
        put("/account/create").then().
                assertThat().body("$", hasKey("newAccountIdentifier")).
                assertThat().statusCode(200);
    }
    
    @Test
    public void getAccountTest() {
        String accountId = put("/account/create").path("newAccountIdentifier.accountId");

        get("/account/" + accountId).then().
                assertThat().
                body("accountIdentifier.accountId", equalTo(accountId)).
                body("moneyAmounts.keySet()", emptyIterable()).
                statusCode(200);
    }

    @Test
    public void accountNotFoundTest() {
        get("/account/" + UUID.randomUUID()).
                then().
                assertThat().
                statusCode(404);
    }
    
    @Test
    public void depositMoney() {
        String accountId = put("/account/create").path("newAccountIdentifier.accountId");
        post("/account/" + accountId + "/deposit/EUR/100.02").then().
                assertThat().
                statusCode(200).
                body("status", equalTo("SUCCESSFUL"));

        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                when().
                get("/account/" + accountId).
                then().
                assertThat().
                body("moneyAmounts.EUR", is(new BigDecimal("100.02"))).
                statusCode(200);
    }

    @Test
    public void depositMoneyInvalidCurrency() {
        String accountId = put("/account/create").path("newAccountIdentifier.accountId");
        post("/account/" + accountId + "/deposit/E34/100.02").then().
                assertThat().
                statusCode(400);

        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                when().
                get("/account/" + accountId).
                then().
                assertThat().
                body("moneyAmounts.keySet()", empty()).
                statusCode(200);
    }

    @Test
    public void depositMoneyInvalidAccount() {
        post("/account/invalidAcc/deposit/EUR/100.02").then().
                assertThat().
                statusCode(400);
    }

    @Test
    public void depositThenWithdrawMoney() {
        String accountId = put("/account/create").path("newAccountIdentifier.accountId");
        post("/account/" + accountId + "/deposit/EUR/100.02").then().
                assertThat().
                statusCode(200).
                body("status", equalTo("SUCCESSFUL"));

        post("/account/" + accountId + "/withdraw/EUR/100.02").then().
                assertThat().
                statusCode(200).
                body("status", equalTo("SUCCESSFUL"));

        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                when().
                get("/account/" + accountId).
                then().
                assertThat().
                body("moneyAmounts.EUR", is(new BigDecimal("0.00"))).
                statusCode(200);
    }

    @Test
    public void withdrawMoneyInsufficientAmount() {
        String accountId = put("/account/create").path("newAccountIdentifier.accountId");
        post("/account/" + accountId + "/deposit/EUR/100.01").then().
                assertThat().
                statusCode(200).
                body("status", equalTo("SUCCESSFUL"));

        post("/account/" + accountId + "/withdraw/EUR/100.02").then().
                assertThat().
                statusCode(400).
                body("status", equalTo("INSUFFICIENT_MONEY"));

        given().
                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                when().
                get("/account/" + accountId).
                then().
                assertThat().
                body("moneyAmounts.EUR", is(new BigDecimal("100.01"))).
                statusCode(200);
    }
}
