package com.bitso.bitgo.impl;

import com.bitso.bitgo.BitGoClient;
import com.bitso.bitgo.SendCoinsResponse;
import com.bitso.bitgo.entity.ListWalletResponse;
import com.bitso.bitgo.entity.Wallet;
import com.bitso.bitgo.entity.WalletTransactionResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * Implementation of the BitGo client.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 4:46 PM
 */
public class BitGoClientImpl implements BitGoClient {

    /**
     * The URL for "sendmany" endpoint (must include anything that's appended to baseUrl).
     */
    private static final String SEND_MANY_URL = "/$COIN/wallet/$WALLET/sendmany";
    private static final String LIST_WALLETS_URL = "/$COIN/wallet";
    private static final String GET_WALLET_URL = "/$COIN/wallet/";
    private static final String CURRENT_USER_PROFILE_URL = "/user/me";
    private static final String GET_WALLET_TRANSFER_URL = "/$COIN/wallet/$WALLET/transfer/$TRANSFER";
    private static final String GET_WALLET_TRANSFER_SEQ_URL = "/$COIN/wallet/$WALLET/transfer/sequenceId/$SEQUENCE";
    private static final String LIST_WALLET_TRANSFER_URL = "/$COIN/wallet/$WALLET/transfer";
    private static final String UNLOCK_URL = "/user/unlock";
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * The base URL (host, port, up to api/v1 for example
     */
    @Getter
    @Setter
    private String baseUrl = "http://localhost:3080/api/v2";
    @Setter
    private String longLivedToken;
    @Setter
    @Getter
    private boolean unsafe;


    public BitGoClientImpl(String longLivedToken) {
        this.longLivedToken = longLivedToken;
    }


    @Override
    public Optional<String> login(String email, String password, String otp, boolean extensible)
            throws IOException {
        return Optional.empty();
    }

    @Override
    public List<Wallet> listWallets(String coin) throws IOException {
        String url = baseUrl + LIST_WALLETS_URL.replace("$COIN", coin);

        HttpURLConnection conn = httpGet(url);

        final ListWalletResponse resp = SerializationUtil.mapper.readValue(conn.getInputStream(), ListWalletResponse.class);
        log.trace("listWallets response: {}", resp);
        return resp.getWallets();
    }

    @Override
    public Optional<Wallet> getWallet(String coin, String wid) throws IOException {
        String url = baseUrl + GET_WALLET_URL.replace("$COIN", coin) + wid;

        HttpURLConnection conn = httpGet(url);

        final Wallet resp = SerializationUtil.mapper.readValue(conn.getInputStream(), Wallet.class);
        log.trace("Wallet {} getWallet response: {}", wid, resp);
        return Optional.of(resp);
    }

    @Override
    public Optional<SendCoinsResponse> sendMany(String coin, String walletId, String walletPass,
                                                Map<String, BigDecimal> recipients,
                                                String sequenceId, String message,
                                                BigDecimal fee, BigDecimal feeTxConfirmTarget,
                                                int minConfirms, boolean enforceMinConfirmsForChange)
            throws IOException {
        String url = baseUrl + SEND_MANY_URL.replace("$COIN", coin).replace("$WALLET", walletId);
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        final List<Map<String, Object>> addr = new ArrayList<>(recipients.size());
        for (Map.Entry<String, BigDecimal> e : recipients.entrySet()) {
            Map<String, Object> a = new HashMap<>(2);
            a.put("address", e.getKey());
            a.put("amount", e.getValue().longValue());
            addr.add(a);
        }
        final Map<String, Object> data = new HashMap<>();
        data.put("recipients", addr);
        if (message != null) {
            data.put("message", message);
        }
        if (sequenceId != null) {
            data.put("sequenceId", sequenceId);
        }
        if (fee != null) {
            data.put("fee", fee.longValue());
        }
        if (feeTxConfirmTarget != null) {
            data.put("feeTxConfirmTarget", feeTxConfirmTarget);
        }
        if (minConfirms > 0) {
            data.put("minConfirms", minConfirms);
        }
        data.put("enforceMinConfirmsForChange", enforceMinConfirmsForChange);
        log.info("sendMany {}", data);
        data.put("walletPassphrase", walletPass);
        HttpURLConnection conn = httpPost(url, data, auth);
        if (conn == null) {
            return Optional.empty();
        }
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            SendCoinsResponse errorResponse = new SendCoinsResponse();
            errorResponse.setResponseCode(conn.getResponseCode());
            errorResponse.setError(new Scanner(conn.getErrorStream(), "UTF-8").useDelimiter("\r\n").next());
            log.error("Got error: {}", errorResponse.getError());
            return Optional.of(errorResponse);
        }
        SendCoinsResponse resp = SerializationUtil.mapper.readValue(conn.getInputStream(), SendCoinsResponse.class);
        log.trace("sendMany response: {}", resp);
        return Optional.of(resp);
    }

    @Override
    public Optional<Map<String, Object>> getCurrentUserProfile() throws IOException {
        String url = baseUrl + CURRENT_USER_PROFILE_URL;

        HttpURLConnection conn = httpGet(url);
        Map<String, Object> resp = SerializationUtil.mapper.readValue(conn.getInputStream(), new TypeReference<Map<String, Object>>() {
        });
        log.trace("getCurrentUserProfile response: {}", resp);
        return Optional.of(resp);
    }

    @Override
    public Optional<Map<String, Object>> getWalletTransferId(String coin, String walletId, String walletTransferId) throws IOException {
        String url = baseUrl + GET_WALLET_TRANSFER_URL.replace("$COIN", coin).replace("$WALLET", walletId).replace("$TRANSFER", walletTransferId);
        HttpURLConnection conn = httpGet(url);
        Map<String, Object> resp = SerializationUtil.mapper.readValue(conn.getInputStream(), new TypeReference<Map<String, Object>>() {
        });
        log.trace("getWalletTransferId response: {}", resp);
        return Optional.of(resp);
    }

    @Override
    public Optional<Map<String, Object>> getWalletTransferSeqId(String coin, String walletId, String sequenceId) throws IOException {
        String url = baseUrl + GET_WALLET_TRANSFER_SEQ_URL.replace("$COIN", coin).replace("$WALLET", walletId).replace("$SEQUENCE", sequenceId);

        HttpURLConnection conn = httpGet(url);
        Map<String, Object> resp = SerializationUtil.mapper.readValue(conn.getInputStream(), new TypeReference<Map<String, Object>>() {
        });
        log.trace("getWalletTransferSeqId response: {}", resp);
        return Optional.of(resp);
    }

    @Override
    public WalletTransactionResponse listWalletTransfers(String coin, String walletId, String prevId) throws IOException {
        String url = baseUrl + LIST_WALLET_TRANSFER_URL.replace("$COIN", coin).replace("$WALLET", walletId);

        HttpURLConnection conn = httpGet(url);
        if (prevId != null) {
            conn.setRequestProperty("prevId", prevId);  //TODO this needs to be tested
        }
        final WalletTransactionResponse resp = SerializationUtil.mapper.readValue(conn.getInputStream(), WalletTransactionResponse.class);
        log.trace("listWalletTransactions response: {}", resp);
        return resp;
    }

    @Override
    public int unlock(String otp, Long duration) throws IOException {
        String url = baseUrl + UNLOCK_URL;

        final Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        if (duration != null) {
            data.put("duration", duration);
        }

        HttpURLConnection conn = httpPost(url, data, getAuth());
        return conn.getResponseCode();
    }

    private HttpURLConnection httpPost(String url, Map<String, Object> data, String auth) throws IOException {
        return unsafe ? HttpHelper.postUnsafe(url, data, auth) : HttpHelper.post(url, data, auth);
    }

    private HttpURLConnection httpGet(String url) throws IOException {
        return unsafe ? HttpHelper.getUnsafe(url, getAuth()) : HttpHelper.get(url, getAuth());
    }

    private String getAuth() {
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        return auth;
    }
}
