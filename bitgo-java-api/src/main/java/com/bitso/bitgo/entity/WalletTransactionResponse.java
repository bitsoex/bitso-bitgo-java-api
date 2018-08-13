package com.bitso.bitgo.entity;

import lombok.Data;

/**
 * @author kushal256
 * Date: 8/13/18
 */
@Data
public class WalletTransactionResponse {
    private Transaction[] transactions;
    private String coin;
}
