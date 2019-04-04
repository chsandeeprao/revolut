package io.test.revolut.sandeep.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {

    INR("INR", 2),
    USD("USD", 2);

    private final String code;
    private final int minorDigits;

}
