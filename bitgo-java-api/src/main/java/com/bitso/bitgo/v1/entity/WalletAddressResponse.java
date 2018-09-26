package com.bitso.bitgo.v1.entity;

import lombok.Data;

import java.util.List;

/**
 * @author kushal256
 * Date: 9/12/18
 */
@Data
public class WalletAddressResponse {
    private List<Address> addresses;
    private long start, count, total;
    private boolean hasMore;
}
