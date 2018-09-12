package com.bitso.bitgo.v1.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * See https://bitgo.github.io/bitgo-docs/?shell#get-wallet-transaction
 *
 * @author kushal256
 * Date: 9/12/18
 */
@Data
public class Output {
    private String account;
    @JsonProperty("isMine")  //Jackson tries to remove 'is'
    private boolean isMine;
    private BigDecimal value;
    private int chain;  //(0 for receive addresses, 1 for change addresses, 10 for SegWit receive addresses, 11 for SegWit change addresses)
    private int chainIndex;
    private int vout;
}
