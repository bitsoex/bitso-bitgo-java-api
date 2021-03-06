package com.bitso.bitgo.util;

import com.bitso.bitgo.v2.BitGoClient;
import com.bitso.bitgo.v2.BitGoClientImpl;
import com.bitso.bitgo.v2.entity.SendCoinsResponse;
import com.bitso.bitgo.v2.entity.Transfer;
import com.bitso.bitgo.v2.entity.Wallet;
import com.bitso.bitgo.v2.entity.WalletTransferResponse;
import lombok.extern.slf4j.Slf4j;
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
 * docker run -it -p 3080:3080 bitgosdk/express:latest
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
        Map<String, Object> parameters = new HashMap<String, Object>();
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
        parameters.put("message", "test");
        parameters.put("minConfirms", 1);
        parameters.put("enforceMinConfirmsForChange", true);
        String sequenceId = ""; //Set it up for transaction to work
        Optional<SendCoinsResponse> resp = client.sendMany(COIN, WALLET_ID, WALLET_PASSPHRASE, targets, sequenceId, parameters);
        Assert.assertTrue(resp.isPresent());
        Assert.assertNotNull(resp.get().getTx());

    }

    @Test
    @Ignore
    public void testSendManyFail() throws IOException {
        int unlockResult = client.unlock("0000000", TimeUnit.HOURS.toSeconds(1));
        System.out.println(unlockResult);
        assertTrue(unlockResult != 400 && unlockResult != 401);
        Map<String, BigDecimal> targets = new HashMap<>();
        BigDecimal initAmount = new BigDecimal("0.001").movePointRight(8);
        BigDecimal amount = initAmount;
        targets.put(TLTC_TEST_FAUCET_ADDRESS, amount);
        String sequenceId = "123";
        // Send required COIN parameter as empty
        try {
            client.sendMany("", WALLET_ID, WALLET_PASSPHRASE, targets, sequenceId, null);
        } catch (Exception e) {
            Assert.assertEquals("Invalid currency", e.getMessage());
        }
        // Send COIN  parameter  misformed
        try {
            client.sendMany("abc", WALLET_ID, WALLET_PASSPHRASE, targets, sequenceId, null);
        } catch (Exception e) {
            Assert.assertEquals("Invalid currency", e.getMessage());
        }
        // Send required COIN parameter as null
        try {
            client.sendMany(null, WALLET_ID, WALLET_PASSPHRASE, targets, sequenceId, null);
        } catch (Exception e) {
            Assert.assertEquals("coin is marked @NonNull but is null", e.getMessage());
        }
        // Send an optional parameter with a wrong type
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("fee", "0");
            client.sendMany(COIN, WALLET_ID, WALLET_PASSPHRASE, targets, sequenceId, parameters);
        } catch (Exception e) {
            Assert.assertEquals("Fee should be a BigDecimal value", e.getMessage());
        }
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
        final WalletTransferResponse resp = client.listWalletTransfers(COIN, WALLET_ID, null, 250, Map.of("dateGte", 1536189990165L));
        System.out.println("list.size() = " + resp.getTransfers().size());
        assertTrue(resp.getTransfers().size() == 3);
//        System.out.println(resp);
//        for (int i = 0; i < 30; i++) {
//            resp.getTransfers().remove(0);
//        }
        System.out.println("resp json = " + SerializationUtil.mapper.writeValueAsString(resp));
    }

    @Test
    public void getWalletTransfers() throws IOException {
        final Optional<Transfer> resp = client.getWalletTransferId(COIN, WALLET_ID, "cdaa330f5717556d873506ee5bb283b3703f9b2a73899577ce3530ae2c75e0ea");
        System.out.println("resp = " + resp.get());
        System.out.println("resp json = " + SerializationUtil.mapper.writeValueAsString(resp.get()));
    }
}
