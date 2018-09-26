package com.bitso.bitgo.v2.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Blabla.
 *
 * @author Enrique Zamudio
 * Date: 5/11/17 4:02 PM
 */
@Data
public class User {

    private String user;
    private List<String> permissions;

    private BigDecimal balance;
    private BigDecimal confirmedBalance;
    private BigDecimal spendableBalance;
    private BigDecimal spendableConfirmedBalance;
    private BigDecimal instantBalance;
}
