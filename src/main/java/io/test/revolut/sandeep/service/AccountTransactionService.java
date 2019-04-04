package io.test.revolut.sandeep.service;

import io.test.revolut.sandeep.domain.Account;

import java.util.function.BiConsumer;

public interface AccountTransactionService {

    void doInTransaction(Account account1, Account account2, BiConsumer<Account, Account> action);
}
