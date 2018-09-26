package com.bitso.bitgo.v1.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Consolidated entries of the transaction, taking into account net inputs/outputs from https://bitgo.github.io/bitgo-docs/?shell#get-wallet-transaction
 *
 * @author kushal256
 * Date: 9/12/18
 */
@Data
public class Entry {
    private String account;
    private BigDecimal value;
}
