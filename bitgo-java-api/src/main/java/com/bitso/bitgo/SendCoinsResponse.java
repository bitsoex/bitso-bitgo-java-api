package com.bitso.bitgo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * A response to the SendCoins/SendCoinsToMultipleAddresses request.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 4:51 PM
 */
@Data
public class SendCoinsResponse extends AbstractResponse {

    private String tx;
    private String hash;
    private BigDecimal fee;
    private BigDecimal feeRate;
    private String pendingApproval;
    private boolean otp;
    private String triggeredPolicy;
}
