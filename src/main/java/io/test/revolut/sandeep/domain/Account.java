package io.test.revolut.sandeep.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private final ReentrantLock lock = new ReentrantLock();

    public Account(String accountNumber, Currency currency, BigInteger accountBalance) {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.accountBalance = accountBalance;
    }

    @Getter
    private String accountNumber;

    @Getter
    private Currency currency;
    @Getter
    @Setter
    private volatile BigInteger accountBalance;

    public BigDecimal getDecimalBalance() {
        BigInteger balance = getAccountBalance();
        BigDecimal balanceWithDecimal;
        if (balance.compareTo(BigInteger.ZERO) == 0) {
            balanceWithDecimal = BigDecimal.ZERO;
        } else {
            int roundingDigits = getCurrency().getMinorDigits();
            balanceWithDecimal = new BigDecimal(balance)
                    .divide(BigDecimal.TEN.pow(roundingDigits), roundingDigits, RoundingMode.HALF_UP);
        }
        return balanceWithDecimal;
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }


}
