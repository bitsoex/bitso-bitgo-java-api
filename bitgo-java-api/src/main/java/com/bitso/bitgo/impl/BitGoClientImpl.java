package com.bitso.bitgo.impl;

import com.bitso.bitgo.BitGoClient;
import com.bitso.bitgo.SendCoinsResponse;
import com.bitso.bitgo.entity.Transaction;
import com.bitso.bitgo.entity.Wallet;
import com.bitso.bitgo.entity.ListWalletResponse;
import com.bitso.bitgo.entity.WalletTransactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Implementation of the BitGo client.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 4:46 PM
 */
public class BitGoClientImpl implements BitGoClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /** The base URL (host, port, up to api/v1 for example */
    @Getter @Setter
    private String baseUrl = "http://localhost:3080/api/v2";
    /** The URL for "sendmany" endpoint (must include anything that's appended to baseUrl). */
    private static final String SEND_MANY_URL = "/$COIN/wallet/$WALLET/sendmany";
    private static final String LIST_WALLETS_URL = "/$COIN/wallet";
    private static final String GET_WALLET_URL = "/$COIN/wallet/";
    private static final String CURRENT_USER_PROFILE_URL = "/user/me";
    private static final String GET_WALLET_TXN_URL = "/$COIN/wallet/$WALLET/transfer/$TRANSFER";
    private static final String GET_WALLET_TXN_SEQ_URL = "/$COIN/wallet/$WALLET/transfer/sequenceId/$SEQUENCE";
    private static final String LIST_WALLET_TXN_URL = "/$COIN/wallet/$WALLET/tx";
    private static final String UNLOCK_URL = "/user/unlock";

    private String longLivedToken;
    @Setter @Getter
    private boolean unsafe;

    private ObjectMapper objectMapper = new ObjectMapper();

    public BitGoClientImpl(String longLivedToken) {
        this.longLivedToken = longLivedToken;
    }

    public void setLongLivedToken(String token) {
        longLivedToken = token;
    }

    @Override
    public Optional<String> login(String email, String password, String otp, boolean extensible)
            throws IOException {
        return Optional.empty();
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
        final List<Map<String,Object>> addr = new ArrayList<>(recipients.size());
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
        HttpURLConnection conn = unsafe ? HttpHelper.postUnsafe(url, data, auth) : HttpHelper.post(url, data, auth);
        if (conn == null) {
            return Optional.empty();
        }
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK){
            SendCoinsResponse errorResponse = new SendCoinsResponse();
            errorResponse.setResponseCode(conn.getResponseCode());
            errorResponse.setError(new Scanner(conn.getErrorStream(),"UTF-8").useDelimiter("\r\n").next());
            log.error("Got error: {}", errorResponse.getError());
            return Optional.of(errorResponse);
        }
        Map<String,Object> resp = HttpHelper.readResponse(conn);
        log.trace("sendMany response: {}", resp);
        if (resp.containsKey("error") || resp.containsKey("tx")) {
            SendCoinsResponse r = new SendCoinsResponse();
            r.setTx((String)resp.get("tx"));
            r.setHash((String)resp.get("hash"));
            r.setError((String)resp.get("error"));
            r.setPendingApproval((String)resp.get("pendingApproval"));
            r.setOtp((Boolean)resp.getOrDefault("otp", false));
            r.setTriggeredPolicy((String)resp.get("triggeredPolicy"));
            r.setStatus((String)resp.get("status"));
            //convert from satoshis
            r.setFee(Conversions.satoshiToBitcoin(((Number)resp.getOrDefault(
                    "fee", 0)).longValue()));
            //convert from satoshis
            r.setFeeRate(Conversions.satoshiToBitcoin(((Number)resp.getOrDefault(
                    "feeRate", 0)).longValue()));
            return Optional.of(r);
        }
        return Optional.empty();
    }

    @Override
    public List<Wallet> listWallets(String coin) throws IOException {
        String url = baseUrl + LIST_WALLETS_URL.replace("$COIN", coin);
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);

        final ListWalletResponse resp = objectMapper.readValue(conn.getInputStream(), ListWalletResponse.class);
        log.trace("Wallets response: {}", resp);
        return resp.getWallets();
    }

    @Override
    public Optional<Wallet> getWallet(String coin, String wid) throws IOException {
        String url = baseUrl + GET_WALLET_URL.replace("$COIN", coin) + wid;
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);

        final Wallet resp = objectMapper.readValue(conn.getInputStream(), Wallet.class);
        log.trace("Wallet {} response: {}", wid, resp);
        return Optional.of(resp);
    }

    @Override
    public Optional<Map<String, Object>> getCurrentUserProfile() throws IOException{
        String url = baseUrl + CURRENT_USER_PROFILE_URL;
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);
        Map<String,Object> resp = HttpHelper.readResponse(conn);
        log.trace("getCurrentUserProfile response: {}", resp);
        return Optional.of(resp);
    }

    @Override
    public Optional<Map<String, Object>> getWalletTransferId(String coin, String walletId, String walletTransferId) throws IOException {
        String url = baseUrl + GET_WALLET_TXN_URL.replace("$COIN", coin).replace("$WALLET", walletId).replace("$TRANSFER", walletTransferId);
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);
        Map<String,Object> resp = HttpHelper.readResponse(conn);
        log.trace("getCurrentUserProfile response: {}", resp);
        return Optional.of(resp);
    }


    @Override
    public Optional<Map<String, Object>> getWalletTransferSeqId(String coin, String walletId, String sequenceId) throws IOException {
        String url = baseUrl + GET_WALLET_TXN_SEQ_URL.replace("$COIN", coin).replace("$WALLET", walletId).replace("$SEQUENCE", sequenceId);
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);
        Map<String,Object> resp = HttpHelper.readResponse(conn);
        log.trace("getCurrentUserProfile response: {}", resp);
        return Optional.of(resp);
    }

    @Override
    public WalletTransactionResponse listWalletTransactions(String coin, String walletId) throws IOException {
        String url = baseUrl + LIST_WALLET_TXN_URL.replace("$COIN", coin).replace("$WALLET", walletId);
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);

        final WalletTransactionResponse resp = objectMapper.readValue(conn.getInputStream(), WalletTransactionResponse.class);
        for (final Transaction txn : resp.getTransactions()) {
            txn.convertInputAndOutputToMap();
        }
        log.trace("getCurrentUserProfile response: {}", resp);
        return resp;
    }

    @Override
    public int unlock(String otp, Long duration) throws IOException {
        String url = baseUrl + UNLOCK_URL;
        final String auth;
        if (longLivedToken == null) {
            log.warn("TODO: implement auth with username/password");
            auth = "TODO!";
        } else {
            auth = longLivedToken;
        }

        final Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        if (duration != null) {
            data.put("duration", duration);
        }

        HttpURLConnection conn = unsafe ? HttpHelper.postUnsafe(url, data, auth) : HttpHelper.post(url, data, auth);
        return conn.getResponseCode();
    }
}
