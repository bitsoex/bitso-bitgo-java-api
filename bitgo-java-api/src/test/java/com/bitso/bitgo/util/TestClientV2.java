package com.bitso.bitgo.util;

import com.bitso.bitgo.v2.BitGoClient;
import com.bitso.bitgo.v2.BitGoClientImpl;
import com.bitso.bitgo.v2.entity.SendCoinsResponse;
import com.bitso.bitgo.v2.entity.Wallet;
import com.bitso.bitgo.v2.entity.WalletTransferResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

/**
 * Simple client tests.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 8:04 PM
 */
@Slf4j
public class TestClientV2 {

    public static final String WALLET_ID = System.getenv("WALLET_ID");
    public static final String WALLET_PASSPHRASE = System.getenv("WALLET_PASSPHRASE");
    public static final String WALLET_ADDRESS = System.getenv("WALLET_ADDRESS");
    public static final String TOKEN = System.getenv("TOKEN");
    private static final String COIN = System.getenv("COIN");
    private static final String TLTC_TEST_FAUCET_ADDRESS = "mgTbDyNGwJeewjdXmU9cRQe8WDauVqn4WK";
    private final BitGoClient client = new BitGoClientImpl(TOKEN, "http://localhost:3080/api/v2", true);


    @Test
    @Ignore
    public void testSendMany() throws IOException {
        JSONObject parameters = new JSONObject();
        int unlockResult = client.unlock("0000000", TimeUnit.HOURS.toSeconds(1));
        System.out.println(unlockResult);
        assertTrue(unlockResult != 400 && unlockResult != 401);
        Map<String, BigDecimal> targets = new HashMap<>();
        BigDecimal initAmount = new BigDecimal("0.001").movePointRight(8);
        BigDecimal amount = initAmount;
        for (int i = 0; i < 50; i++) {
            targets.put(TLTC_TEST_FAUCET_ADDRESS, amount);
            amount = amount.add(initAmount);
        }
        parameters.put("coin", COIN);
        parameters.put("walletId", WALLET_ID);
        parameters.put("walletPass", WALLET_PASSPHRASE);
        parameters.put("recipients", targets);
        parameters.put("message", "test");
        parameters.put("minConfirms", 1);
        parameters.put("enforceMinConfirmsForChange", true);
        parameters.put("sequenceId", "btc33");
        Optional<SendCoinsResponse> resp = client.sendMany(parameters);
        Assert.assertTrue(resp.isPresent());
        Assert.assertNotNull(resp.get().getTx());

    }

    @Test
    public void getWallets() throws IOException {
        System.out.println(WALLET_ID);
        List<Wallet> wallets = client.listWallets(COIN);
        Assert.assertNotNull(wallets);
        Assert.assertFalse(wallets.isEmpty());
    }

    @Test
    public void getWalletByAddress() throws Exception {
        Optional<Wallet> wallet = client.getWalletByAddress(COIN, WALLET_ADDRESS);
        Assert.assertNotNull(wallet);
        Assert.assertFalse(wallet.isEmpty());
    }

    @Test
    public void currentUserProfile() throws IOException {
        final Optional<Map<String, Object>> profile = client.getCurrentUserProfile();
        Assert.assertNotNull(profile.get());
    }

    @Test
    public void listWalletTransfers() throws IOException {
        final WalletTransferResponse resp = client.listWalletTransfers(COIN, WALLET_ID, null, 250);
        System.out.println("list.size() = " + resp.getTransfers().size());
        assertTrue(resp.getTransfers().size() > 10);
//        System.out.println(resp);
//        for (int i = 0; i < 30; i++) {
//            resp.getTransfers().remove(0);
//        }
        System.out.println("resp json = " + SerializationUtil.mapper.writeValueAsString(resp));


    }
}
