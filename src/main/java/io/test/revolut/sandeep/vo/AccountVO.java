package io.test.revolut.sandeep.vo;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.test.revolut.sandeep.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"accountNumber", "currency", "accountBalance"})
public class AccountVO {

    private String accountNumber;
    private String currency;
    private BigDecimal accountBalance;

    public static AccountVO fromDomain(Account account) {
        return new AccountVO(account.getAccountNumber(), account.getCurrency().getCode(), account.getDecimalBalance());
    }


}
