package com.bitso.bitgo.v2.entity;

import lombok.Data;

import java.util.List;

/**
 * @author kushal256
 * Date: 8/13/18
 */
@Data
public class WalletTransferResponse {
    private List<Transfer> transfers;
    private String coin;
    private String nextBatchPrevId;
}
