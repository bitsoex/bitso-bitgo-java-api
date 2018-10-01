package com.bitso.bitgo.v1;

import com.bitso.bitgo.util.HttpHelper;
import com.bitso.bitgo.util.SerializationUtil;
import com.bitso.bitgo.v1.entity.WalletAddressResponse;
import com.bitso.bitgo.v1.entity.WalletTransactionResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the BitGo client.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 4:46 PM
 */
@Slf4j
public class BitGoClientImpl implements BitGoClient {

    private static final String CURRENT_USER_PROFILE_URL = "/user/me";
    private static final String LIST_WALLET_TRANSACTION_URL = "/wallet/$WALLET/tx";
    private static final String LIST_WALLET_ADDRESSES_URL = "/wallet/$WALLET/addresses";

    /**
     * The base URL (host, port, up to api/v1 for example
     */
    @Getter
    @Setter
    private String baseUrl = "http://localhost:3080/api/v1";
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
    public Optional<Map<String, Object>> getCurrentUserProfile() throws IOException {
        String url = baseUrl + CURRENT_USER_PROFILE_URL;

        HttpURLConnection conn = httpGet(url);
        Map<String, Object> resp = SerializationUtil.mapper.readValue(conn.getInputStream(), new TypeReference<Map<String, Object>>() {
        });
        log.trace("getCurrentUserProfile response: {}", resp);
        return Optional.of(resp);
    }


    /**
     * See https://bitgo.github.io/bitgo-docs/?shell#list-wallet-transactions
     * <p>
     * WALLET=5b6c7d15909e2d8a032abdf08b4929d8
     * <p>
     * curl -X GET \
     * -H "Content-Type: application/json" \
     * -H "Authorization: Bearer $ACCESS_TOKEN" \
     * https://test.bitgo.com/api/v1/wallet/$WALLET/tx
     *
     * @param walletId
     * @return
     * @throws IOException
     */
    @Override
    public WalletTransactionResponse listWalletTransactions(String walletId, long skip, int limit, Long minHeight, Long maxHeight, Integer minConfirms) throws IOException {
        if (limit > 250) limit = 250;
        if (limit < 0) limit = 0;

        String url = baseUrl + LIST_WALLET_TRANSACTION_URL.replace("$WALLET", walletId);

        Map<String, String> reqPropMap = new HashMap<>();
        reqPropMap.put("skip", Long.toString(skip));
        reqPropMap.put("limit", Integer.toString(limit));
        if (minHeight != null) reqPropMap.put("minHeight", minHeight.toString());
        if (maxHeight != null) reqPropMap.put("maxHeight", maxHeight.toString());
        if (minConfirms != null) reqPropMap.put("minConfirms", minConfirms.toString());

        HttpURLConnection conn = null;

        //TODO make these parameters configurable, apply to all our calls, not just this one.  
        int retryCounter = 0;
        while (retryCounter < 3) {
            conn = httpGet(url, reqPropMap);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                break;
            } else if (conn.getResponseCode() >= 500 && conn.getResponseCode() < 600) {  //524 only?
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

        final WalletTransactionResponse resp = SerializationUtil.mapper.readValue(conn.getInputStream(), WalletTransactionResponse.class);
        log.trace("listWalletTransactions response: {}", resp);
        return resp;
    }

    @Override
    public WalletAddressResponse listWalletAddress(String walletId, long skip, int limit) throws IOException {
        if (limit > 500) limit = 500;
        if (limit < 0) limit = 0;

        String url = baseUrl + LIST_WALLET_ADDRESSES_URL.replace("$WALLET", walletId);

        Map<String, String> reqPropMap = new HashMap<>();
        reqPropMap.put("skip", Long.toString(skip));
        reqPropMap.put("limit", Integer.toString(limit));

        HttpURLConnection conn = httpGet(url, reqPropMap);

        final WalletAddressResponse resp = SerializationUtil.mapper.readValue(conn.getInputStream(), WalletAddressResponse.class);
        log.trace("listWalletAddress response: {}", resp);
        return resp;
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

    private String getAuth() {
        return token;
    }
}
