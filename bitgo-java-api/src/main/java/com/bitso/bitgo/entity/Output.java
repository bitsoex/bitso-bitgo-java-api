package com.bitso.bitgo.entity;

import lombok.Data;

/**
 * From https://www.bitgo.com/api/v2/#get-wallet-transaction
 *
 * @author kushalkhan
 * Date: 8/13/18
 */
@Data
public class Output {
    private String id;
    private String address;
    private long value;
    private String valueString;
    private String wallet;
    private int chain;
    private int index;
}
