package io.test.revolut.sandeep;

import io.test.revolut.sandeep.domain.Account;
import io.test.revolut.sandeep.domain.Currency;
import io.test.revolut.sandeep.repository.AccountRepository;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.stream.IntStream;

/**
 * Load the DB with 100 accounts.
 * First 50 are USD accounts, the rest are in INR.<br/>
 */
public class TestAccountsPopulation {

    public static void populateAccounts(AccountRepository rep) {

        IntStream.range(1, 100)
                .mapToObj(i -> {
                    Currency currency = i < 50 ? Currency.USD : Currency.INR;
                    return new Account("CH" + StringUtils.leftPad(String.valueOf(i), 8, "00"),
                            currency,
                            BigInteger.valueOf(i)
                                    .multiply(BigInteger.TEN.pow(currency.getMinorDigits())));
                })
                .forEach(rep::save);
    }
}
