package com.bitso.bitgo.entity;

import lombok.Data;

import java.util.List;

/**
 * @author kushal256
 * Date: 8/13/18
 */
@Data
public class ListWalletResponse {
    private List<Wallet> wallets;
    private String coin;
}
