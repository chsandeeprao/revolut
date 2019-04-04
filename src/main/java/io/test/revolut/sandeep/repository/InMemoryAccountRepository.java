package io.test.revolut.sandeep.repository;

import io.test.revolut.sandeep.domain.Account;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accountStorage = new ConcurrentHashMap<>();

    @Override
    public Collection<Account> getAll() {
        return accountStorage.values();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accountStorage.get(accountNumber));
    }

    @Override
    public Account save(Account account) {
        return accountStorage.put(account.getAccountNumber(), account);
    }

    @Override
    public void delete(Account account) {
        accountStorage.remove(account.getAccountNumber());
    }

    public void clear() {
        accountStorage.clear();
    }
}
