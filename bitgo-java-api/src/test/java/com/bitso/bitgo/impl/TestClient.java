package com.bitso.bitgo.impl;

import com.bitso.bitgo.entity.Transaction;
import com.bitso.bitgo.entity.WalletTransactionResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Simple client tests.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 8:04 PM
 */
public class TestClient {

    private static final String COIN = "tbtc";
    private final BitGoClientImpl client = new BitGoClientImpl("v2xcce93608187287e7ff8a246403c39a9babbb1c737b927ed275c172f558e0b3ab");

    @Before
    public void setup() {
        client.setBaseUrl("https://test.bitgo.com/api/v2");
    }

//    @Test
//    public void testGetWallets() throws IOException {
//        List<Wallet> wallets = client.listWallets(COIN);
//        Assert.assertNotNull(wallets);
//        Assert.assertFalse(wallets.isEmpty());
//    }
//
//    @Test
//    public void testSendMany() throws IOException {
//        Map<String,BigDecimal> targets = new HashMap<>();
//        targets.put("[ADDRESS1]", new BigDecimal("0.001"));
//        targets.put("[ADDRESS2]", new BigDecimal("0.001"));
//        Optional<SendCoinsResponse> resp = client.sendMany(COIN, "wallet", "pass", targets, null,
//                "test", null, null, 1, true);
//        Assert.assertTrue(resp.isPresent());
//        Assert.assertNotNull(resp.get().getTx());
//    }

    @Test
    public void test() throws IOException {
        final WalletTransactionResponse list = client.listWalletTransactions("tbtc", "5b6c7d15909e2d8a032abdf08b4929d8");
        System.out.println(list);
    }
}
