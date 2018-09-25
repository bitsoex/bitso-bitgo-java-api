package com.bitso.bitgo.util;

import com.bitso.bitgo.v1.BitGoClient;
import com.bitso.bitgo.v1.BitGoClientImpl;
import com.bitso.bitgo.v1.entity.WalletAddressResponse;
import com.bitso.bitgo.v1.entity.WalletTransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

/**
 * Simple client tests.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 8:04 PM
 */
@Slf4j
public class TestClientV1 {

    public static final String WALLET_ID = System.getenv("WALLET_ID");
    public static final String TOKEN = System.getenv("TOKEN");
    private final BitGoClient client = new BitGoClientImpl(TOKEN, "https://localhost:3080/api/v1", true);


    @Test
    public void currentUserProfile() throws IOException {
        final Optional<Map<String, Object>> profile = client.getCurrentUserProfile();
        Assert.assertNotNull(profile.get());
    }

    @Test
    public void listWalletAddress() throws IOException {
        final WalletAddressResponse resp = client.listWalletAddress(WALLET_ID, 0, 500);
        System.out.println("list.size() = " + resp.getAddresses().size());
        System.out.println("list = " + resp.getAddresses());
        assertTrue(resp.getAddresses().size() > 1);
    }

    @Test
    public void listWalletTransfers() throws IOException {
        final WalletTransactionResponse resp = client.listWalletTransctions(WALLET_ID, 0, 250);
        System.out.println("list.size() = " + resp.getTransactions().size());
        System.out.println("list = " + resp.getTransactions());
        assertTrue(resp.getTransactions().size() > 0);
    }
}
