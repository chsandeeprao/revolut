package io.test.revolut.sandeep.service;

import io.test.revolut.sandeep.domain.Account;
import io.test.revolut.sandeep.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final CurrencyRatesProvider ratesProvider;
    private final AccountTransactionService transactionService;

    @Override
    public void makeTransfer(String sender, String receiver, BigDecimal amount) {

        Account accountFrom = getAccountByNumber(sender);
        Account accountTo = getAccountByNumber(receiver);

        BigDecimal currencyRate = ratesProvider.getCurrencyRate(accountFrom.getCurrency(), accountTo.getCurrency());

        BigInteger sendAmount = amount.multiply(BigDecimal.TEN.pow(accountFrom.getCurrency().getMinorDigits())).toBigInteger();

        BigInteger receiveAmount = amount.multiply(currencyRate)
                .multiply((BigDecimal.TEN.pow(accountTo.getCurrency().getMinorDigits()))).toBigInteger();

        transactionService.doInTransaction(accountFrom, accountTo, (from, to) -> {

            if (from.getAccountBalance().compareTo(sendAmount) < 0) {
                throw new IllegalArgumentException("Account " + sender + " has insufficient funds (req: " + sendAmount + "; actual: " + from.getAccountBalance());
            }

            from.setAccountBalance(from.getAccountBalance().subtract(sendAmount));
            to.setAccountBalance(to.getAccountBalance().add(receiveAmount));
        });

    }

    private Account getAccountByNumber(String accountNum) {
        return accountRepository.findByAccountNumber(accountNum)
                .orElseThrow(() -> new IllegalArgumentException("Account " + accountNum + " not found!"));
    }
}
