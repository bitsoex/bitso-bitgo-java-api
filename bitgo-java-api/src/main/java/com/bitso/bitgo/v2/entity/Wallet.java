package com.bitso.bitgo.v2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Wallet {

    private String id;
    private String walletId;
    private List<User> users;
    private String coin;
    private String label;
    private int m, n;
    private String[] keys, tags;
    private boolean disableTransactionNotifications;
    private boolean deleted;
    private boolean triggeredCircuitBreaker;
    private boolean allowBackupKeySigning;
    private boolean recoverable;
    private int approvalsRequired;
    private Freeze freeze;
    private CoinSpecific coinSpecific;

    private BigDecimal balance;
    private String balanceString;
    private BigDecimal confirmedBalance;
    private String confirmedBalanceString;
    private BigDecimal spendableBalance;
    private String spendableBalanceString;

    private KeySignature keySignatures;

    @JsonProperty("isCold")  //Jackson tries to remove 'is'
    private boolean isCold;
    private List<String> clientFlags;
    private Admin admin;

    private String startDate;
    private String enterprise;

}
