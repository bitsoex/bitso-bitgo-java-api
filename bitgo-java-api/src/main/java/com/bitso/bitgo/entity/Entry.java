package com.bitso.bitgo.entity;

import lombok.Data;

/**
 * From https://www.bitgo.com/api/v2/#get-wallet-transaction
 *
 * @author kushalkhan
 * Date: 8/13/18
 */
@Data
public class Entry {

    private String address;
    private int inputs;
    private int outputs;
    private long value;
    private String valueString;
}
