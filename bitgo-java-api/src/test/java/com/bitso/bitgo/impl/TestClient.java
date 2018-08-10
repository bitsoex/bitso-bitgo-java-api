package com.bitso.bitgo.impl;

import com.bitso.bitgo.SendCoinsResponse;
import com.bitso.bitgo.Wallet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Simple client tests.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 8:04 PM
 */
@Ignore
public class TestClient {

    private final BitGoClientImpl client = new BitGoClientImpl("[YOUR TOKEN HERE]");
    private static final String COIN = "tbtc";

    @Before
    public void setup() {
        client.setBaseUrl("https://test.bitgo.com/api/v2");
    }

    @Test
    public void testGetWallets() throws IOException {
        List<Wallet> wallets = client.listWallets(COIN);
        Assert.assertNotNull(wallets);
        Assert.assertFalse(wallets.isEmpty());
    }

    @Test
    public void testSendMany() throws IOException {
        Map<String,BigDecimal> targets = new HashMap<>();
        targets.put("[ADDRESS1]", new BigDecimal("0.001"));
        targets.put("[ADDRESS2]", new BigDecimal("0.001"));
        Optional<SendCoinsResponse> resp = client.sendMany(COIN, "wallet", "pass", targets, null,
                "test", null, null, 1, true);
        Assert.assertTrue(resp.isPresent());
        Assert.assertNotNull(resp.get().getTx());
    }
}
