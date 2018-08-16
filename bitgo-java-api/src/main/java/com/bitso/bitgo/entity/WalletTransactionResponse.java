package com.bitso.bitgo.entity;

import lombok.Data;

import java.util.List;

/**
 * @author kushal256
 * Date: 8/13/18
 */
@Data
public class WalletTransactionResponse {
    private List<Transaction> transactions;
    private String coin;
    private String nextBatchPrevId;  //Need to test
}
