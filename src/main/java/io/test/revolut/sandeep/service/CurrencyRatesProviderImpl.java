package io.test.revolut.sandeep.service;

import io.test.revolut.sandeep.domain.Currency;
import lombok.Value;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class CurrencyRatesProviderImpl implements CurrencyRatesProvider {

    @Value
    private static class CacheKey {
        String curFrom;
        String curTo;
    }

    private final Map<CacheKey, BigDecimal> currentyRates;

    {
        currentyRates = new HashMap<>();
        currentyRates.put(new CacheKey("INR", "USD"), new BigDecimal("0.015"));
        currentyRates.put(new CacheKey("USD", "INR"), new BigDecimal("68.45"));
    }

    @Override
    public BigDecimal getCurrencyRate(Currency from, Currency to) {
        if (from.equals(to)) {
            return BigDecimal.ONE;
        }
        BigDecimal rate = currentyRates.get(new CacheKey(from.getCode(), to.getCode()));
        if (rate == null) {
            throw new IllegalArgumentException("No conversion rates found for (" + from.getCode() + "," + to.getCode() + ")");
        }
        return rate;
    }
}
