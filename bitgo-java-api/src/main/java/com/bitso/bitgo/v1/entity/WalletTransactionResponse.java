package com.bitso.bitgo.v1.entity;

import lombok.Data;

import java.util.List;

/**
 * @author kushal256
 * Date: 9/12/18
 */
@Data
public class WalletTransactionResponse {
    private List<Transaction> transactions;
    private long start, count, total;
}
