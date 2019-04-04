package io.test.revolut.sandeep.repository;

import io.test.revolut.sandeep.domain.Account;

import java.util.Collection;
import java.util.Optional;

public interface AccountRepository {

    Collection<Account> getAll();

    Optional<Account> findByAccountNumber(String accountNumber);

    Account save(Account account);

    void delete(Account account);

}
