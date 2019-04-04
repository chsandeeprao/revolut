package io.test.revolut.sandeep.service;

import io.test.revolut.sandeep.domain.Currency;

import java.math.BigDecimal;

public interface CurrencyRatesProvider {

    BigDecimal getCurrencyRate(Currency from, Currency to);
}
