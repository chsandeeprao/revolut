package io.test.revolut.sandeep.service;

import java.math.BigDecimal;

public interface TransferService {

    void makeTransfer(String accountFromNum, String accountToNum, BigDecimal amount);

}
