package com.bitso.bitgo.v2.entity;

import lombok.Data;

/**
 * A response to the SendCoins/SendCoinsToMultipleAddresses request.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 4:51 PM
 */
@Data
public class SendCoinsResponse extends AbstractResponse {

    private String tx;    //hex-encoded tx
    private String txid;  //hash of tx
    private String status;
}
