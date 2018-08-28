package com.bitso.bitgo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * From https://www.bitgo.com/api/v2/#get-wallet-transaction
 *
 * @author kushal256
 * Date: 8/13/18
 */
@Data
public class Input {
    private String id;
    private String address;
    private BigDecimal value;
    private String valueString;
    private String wallet;
    private int chain;
    private int index;
    private String redeemScript;

    @JsonProperty("isSegwit")  //Jackson tries to remove 'is'
    private boolean isSegwit;

}
