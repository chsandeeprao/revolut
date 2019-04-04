package io.test.revolut.sandeep;

import io.restassured.RestAssured;
import io.test.revolut.sandeep.domain.Account;
import io.test.revolut.sandeep.domain.Currency;
import io.test.revolut.sandeep.domain.TransferRequest;
import io.test.revolut.sandeep.repository.AccountRepository;
import io.test.revolut.sandeep.repository.InMemoryAccountRepository;
import org.jooby.test.JoobyRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

public class ApiIntegrationTest {

    private static App app = new App();

    @ClassRule
    public static JoobyRule bootstrap = new JoobyRule(app);

    @BeforeClass
    public static void config() {
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
    }

    @Before
    public void clearAccRepo() {
        app.require(InMemoryAccountRepository.class).clear();
    }


    @Test
    public void appIsUp() {
        get("/swagger")
                .then()
                .assertThat()
                .body(notNullValue());
    }

    @Test
    public void testGetAccountsSize() {
        createMockAccount("A1", BigInteger.ZERO, Currency.INR);
        createMockAccount("A2", BigInteger.ONE, Currency.INR);
        createMockAccount("A3", BigInteger.valueOf(2526L), Currency.USD);

        when()
                .get("/accounts")
                .then()
                .body("$", hasSize(3));
    }


    @Test
    public void testInvalidUrl() {

        when()
                .get("/acshgsdh")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAccount() {

        createMockAccount("CH00000002", BigInteger.valueOf(200), Currency.INR);
        createMockAccount("CH00000205", BigInteger.valueOf(20500), Currency.USD);

        when()
                .get("/accounts/CH00000002")
                .then()
                .body("accountNumber", is("CH00000002"))
                .body("accountBalance", comparesEqualTo(new BigDecimal("2.00")));

        when()
                .get("/accounts/CH00000205")
                .then()
                .body("accountNumber", is("CH00000205"))
                .body("accountBalance", comparesEqualTo(new BigDecimal(205).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void testGetMissingAccount() {

        createMockAccount("CH00000002", BigInteger.valueOf(200), Currency.INR);

        when()
                .get("/accounts/CH000022222")
                .then()
                .statusCode(404);
    }

    @Test
    public void testValidTransfer() {

        createMockAccount("CH00000002", BigInteger.valueOf(200), Currency.INR);
        createMockAccount("CH00000105", BigInteger.valueOf(10500), Currency.INR);

        given().
                when()
                .body(new TransferRequest("CH00000105", "CH00000002", new BigDecimal("2")))
                .contentType("application/json")
                .post("/accounts/transfers")
                .then().statusCode(200);

        when()
                .get("/accounts/CH00000002")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("4.00")));

        when()
                .get("/accounts/CH00000105")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("103.00")));
    }

    @Test
    public void testInvalidTransferNoAccount() {
        createMockAccount("CH00000002", BigInteger.valueOf(200), Currency.INR);
        createMockAccount("CH00000105", BigInteger.valueOf(10500), Currency.INR);

        given().
                when()
                .body(new TransferRequest("CH00000305", "CH00000002", new BigDecimal("2")))
                .contentType("application/json")
                .post("/accounts/transfers")
                .then()
                .statusCode(400)
                .body(containsString("CH00000305 not found"));

    }

    @Test
    public void testInvalidTransferInsufficientFunds() {
        createMockAccount("CH00000002", BigInteger.valueOf(200), Currency.INR);
        createMockAccount("CH00000100", BigInteger.valueOf(10000), Currency.INR);

        given().
                when()
                .body(new TransferRequest("CH00000002", "CH00000100", new BigDecimal("200")))
                .contentType("application/json")
                .post("/accounts/transfers")
                .then()
                .statusCode(400)
                .body(containsString("insufficient funds"));

        // make sure no money was transferred
        when()
                .get("/accounts/CH00000002")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("2.00")));

        when()
                .get("/accounts/CH00000100")
                .then()
                .body("accountBalance", comparesEqualTo(new BigDecimal("100")));

    }

    private void createMockAccount(String accNum, BigInteger balance, Currency currency) {
        app.require(AccountRepository.class).save(new Account(accNum, currency, balance));
    }


}