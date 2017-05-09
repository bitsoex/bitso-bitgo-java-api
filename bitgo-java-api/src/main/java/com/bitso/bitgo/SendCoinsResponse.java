package com.bitso.bitgo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A response to the SendCoins/SendCoinsToMultipleAddresses request.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 4:51 PM
 */
public class SendCoinsResponse extends AbstractResponse {

    @Setter @Getter
    private String tx;
    @Setter @Getter
    private String hash;
    @Setter @Getter
    private BigDecimal fee;
    @Setter @Getter
    private BigDecimal feeRate;
    @Setter @Getter
    private String pendingApproval;
    @Setter @Getter
    private boolean otp;
    @Setter @Getter
    private String triggeredPolicy;
}
