package io.test.revolut.sandeep.service;

import io.test.revolut.sandeep.domain.Account;
import io.test.revolut.sandeep.domain.Currency;
import io.test.revolut.sandeep.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CurrencyRatesProvider currencyRatesProvider;
    private AccountTransactionService transactionService = (account1, account2, action) -> action.accept(account1, account2); // mock
    private TransferService transferService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        transferService = new TransferServiceImpl(accountRepository, currencyRatesProvider, transactionService);
        Mockito.when(currencyRatesProvider.getCurrencyRate(Currency.INR, Currency.INR))
                .thenReturn(BigDecimal.ONE);
        Mockito.when(currencyRatesProvider.getCurrencyRate(Currency.USD, Currency.INR))
                .thenReturn(new BigDecimal("68.45"));
    }


    @Test
    public void checkSimpleTransfer() {
        Account account1 = new Account("acc1", Currency.INR, BigInteger.valueOf(16580));
        Account account2 = new Account("acc2", Currency.INR, BigInteger.valueOf(322));
        Mockito.when(accountRepository.findByAccountNumber("acc1"))
                .thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findByAccountNumber("acc2"))
                .thenReturn(Optional.of(account2));

        transferService.makeTransfer("acc1", "acc2", new BigDecimal("125.0"));

        assertThat(account1.getAccountBalance(), is(BigInteger.valueOf(4080)));
        assertThat(account2.getAccountBalance(), is(BigInteger.valueOf(12822)));

    }

    @Test
    public void checkConversionTransfer() {
        Account account1 = new Account("123", Currency.USD, BigInteger.valueOf(2125));
        Account account2 = new Account("124", Currency.INR, BigInteger.valueOf(3229));
        Mockito.when(accountRepository.findByAccountNumber("123"))
                .thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findByAccountNumber("124"))
                .thenReturn(Optional.of(account2));

        transferService.makeTransfer("123", "124", new BigDecimal("20"));

        assertThat(account1.getAccountBalance(), is(BigInteger.valueOf(125)));
        assertThat(account2.getAccountBalance(), is(BigInteger.valueOf(140129)));

    }

}