package com.bitso.bitgo.v2;

import com.bitso.bitgo.util.HttpHelper;
import com.bitso.bitgo.util.SerializationUtil;
import com.bitso.bitgo.v2.entity.ListWalletResponse;
import com.bitso.bitgo.v2.entity.SendCoinsResponse;
import com.bitso.bitgo.v2.entity.Wallet;
import com.bitso.bitgo.v2.entity.WalletTransferResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

/**
 * Implementation of the BitGo client.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 4:46 PM
 */
@Slf4j
public class BitGoClientImpl implements BitGoClient {

    /**
     * The URL for "sendmany" endpoint (must include anything that's appended to baseUrl).
     */
    private static final String SEND_MANY_URL = "/$COIN/wallet/$WALLET/sendmany";
    private static final String LIST_WALLETS_URL = "/$COIN/wallet";
    private static final String GET_WALLET_URL = "/$COIN/wallet/";
    private static final String GET_WALLET_ADDRESS_URL = GET_WALLET_URL + "address/";
    private static final String CURRENT_USER_PROFILE_URL = "/user/me";
    private static final String GET_WALLET_TRANSFER_URL = "/$COIN/wallet/$WALLET/transfer/$TRANSFER";
    private static final String GET_WALLET_TRANSFER_SEQ_URL = "/$COIN/wallet/$WALLET/transfer/sequenceId/$SEQUENCE";
    private static final String LIST_WALLET_TRANSFER_URL = "/$COIN/wallet/$WALLET/transfer";
    private static final String UNLOCK_URL = "/user/unlock";

    /**
     * The base URL (host, port, up to api/v2 for example
     */
    @Getter
    @Setter
    private String baseUrl = "http://localhost:3080/api/v2";
    @Setter
    private String token;
    @Setter
    @Getter
    private boolean unsafe;


    public BitGoClientImpl(String token, String baseUrl, boolean unsafe) {
        this.token = token;
        this.baseUrl = baseUrl;
        this.unsafe = unsafe;
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
    public Optional<Wallet> getWalletByAddress(String coin, String waddress) throws IOException {
        String url = baseUrl + GET_WALLET_ADDRESS_URL.replace("$COIN", coin) + waddress;

        HttpURLConnection conn = httpGet(url);

        final Wallet resp = SerializationUtil.mapper.readValue(conn.getInputStream(), Wallet.class);
        log.trace("Wallet address {} getWallet response: {}", waddress, resp);
        return Optional.of(resp);
    }

    @Override
    public Optional<SendCoinsResponse> sendMany(JSONObject parameters) throws IOException {
        // Get all needed parameters
        String coin = parameters.getString("coin");
        String walletId = parameters.getString("walletId");
        String walletPass = parameters.getString("walletPass");
        JSONObject targets = parameters.getJSONObject("recipients");
        HashMap<String, BigDecimal> recipients = new HashMap<String, BigDecimal>(targets.length());
        for (String address : targets.keySet()) {
            recipients.put(address, targets.getBigDecimal(address));
        }
        String sequenceId = parameters.getString("sequenceId");
        // Check for optional parameters or default them
        String message = parameters.has("message") ? parameters.getString("message") : "";
        BigDecimal fee = parameters.has("fee") ? parameters.getBigDecimal("fee") : BigDecimal.ZERO;
        BigDecimal feeTxConfirmTarget = parameters.has("feeTxConfirmTarget") ?
                parameters.getBigDecimal("feeTxConfirmTarget") : BigDecimal.ZERO;
        int minConfirms = parameters.has("minConfirms") ? parameters.getInt("minConfirms") : 0;
        boolean enforceMinConfirmsForChange = parameters.has("enforceMinConfirmsForChange") ?
                parameters.getBoolean("enforceMinConfirmsForChange") : false;
        String url = baseUrl + SEND_MANY_URL.replace("$COIN", coin).replace("$WALLET", walletId);

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
        HttpURLConnection conn = httpPost(url, data);
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
    public WalletTransferResponse listWalletTransfers(String coin, String walletId, String prevId, int limit) throws IOException {
        if (limit > 250) limit = 250;
        if (limit < 0) limit = 0;
        String url = baseUrl + LIST_WALLET_TRANSFER_URL.replace("$COIN", coin).replace("$WALLET", walletId) + "?limit=" + limit;

        Map<String, String> reqPropMap = null;
        if (prevId != null) {
            reqPropMap = new HashMap<>();
            reqPropMap.put("prevId", prevId);
        }
        HttpURLConnection conn = httpGetRetry500(url, reqPropMap);

        final WalletTransferResponse resp = SerializationUtil.mapper.readValue(conn.getInputStream(), WalletTransferResponse.class);
        log.trace("listWalletTransactions response: {}", resp);
        return resp;
    }

    @Override
    public int unlock(String otp, Long durationSecs) throws IOException {
        String url = baseUrl + UNLOCK_URL;

        final Map<String, Object> data = new HashMap<>();
        data.put("otp", otp);
        if (durationSecs != null) {
            data.put("duration", durationSecs);
        }

        HttpURLConnection conn = httpPost(url, data);
        return conn.getResponseCode();
    }

    private HttpURLConnection httpPost(String url, Map<String, Object> data) throws IOException {
        return unsafe ? HttpHelper.postUnsafe(url, data, getAuth()) : HttpHelper.post(url, data, getAuth());
    }

    private HttpURLConnection httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    private HttpURLConnection httpGet(String url, Map<String, String> reqParams) throws IOException {
        return unsafe ? HttpHelper.getUnsafe(url, getAuth(), reqParams) : HttpHelper.get(url, getAuth(), reqParams);
    }

    private HttpURLConnection httpGetRetry500(String url, Map<String, String> reqParams) throws IOException {

        HttpURLConnection conn = null;

        int retryCounter = 0;
        while (retryCounter < 3) {
            conn = unsafe ? HttpHelper.getUnsafe(url, getAuth(), reqParams) : HttpHelper.get(url, getAuth(), reqParams);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                break;
            } else if (conn.getResponseCode() >= 500 && conn.getResponseCode() < 600) {
                long sleepTimeMillis = TimeUnit.SECONDS.toMillis(5);
                log.info("Got responseCode={} with message={}, sleeping for {}ms, retryCounter={}", conn.getResponseCode(), conn.getResponseMessage(), sleepTimeMillis, retryCounter);
                try {
                    Thread.sleep(sleepTimeMillis);
                } catch (InterruptedException e) {
                    log.error("Error", e);
                }
                retryCounter++;
            } else {
                //Some other exception, don't retry
                break;
            }
        }
        return conn;
    }

    private String getAuth() {
        return token;
    }
}
