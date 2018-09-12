package com.bitso.bitgo.v1.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kushal256
 * Date: 9/12/18
 */
@Data
public class Output {
    private String account;
    @JsonProperty("isMine")  //Jackson tries to remove 'is'
    private boolean isMine;
    private BigDecimal value;
    private int chain;
    private int chainIndex;
    private int vout;
}
