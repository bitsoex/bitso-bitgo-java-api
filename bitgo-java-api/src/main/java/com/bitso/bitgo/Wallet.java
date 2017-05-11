package com.bitso.bitgo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Blabla.
 *
 * @author Enrique Zamudio
 *         Date: 5/11/17 4:02 PM
 */
public class Wallet {

    @Setter @Getter
    private BigDecimal balance;
    @Setter @Getter
    private BigDecimal confirmedBalance;
}
