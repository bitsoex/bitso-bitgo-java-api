package com.bitso.bitgo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Blabla.
 *
 * @author Enrique Zamudio
 *         Date: 5/11/17 4:02 PM
 */
@Data
public class Wallet {

    private String id;
    private BigDecimal balance;
    private BigDecimal confirmedBalance;
    private BigDecimal spendableBalance;
    private BigDecimal spendableConfirmedBalance;
    private BigDecimal instantBalance;
}
