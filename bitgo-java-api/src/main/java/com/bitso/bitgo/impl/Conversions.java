package com.bitso.bitgo.impl;

import java.math.BigDecimal;

/**
 * Conversions utility.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 7:40 PM
 */
public class Conversions {

    public static BigDecimal satoshiToBitcoin(long satoshiAmount) {
        return new BigDecimal(satoshiAmount).movePointLeft(8);
    }

    public static long bitcoinToSatoshi(BigDecimal bitcoinAmount) {
        return bitcoinAmount.movePointRight(8).longValue();
    }

}
