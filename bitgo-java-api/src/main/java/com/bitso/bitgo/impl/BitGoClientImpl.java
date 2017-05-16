package com.bitso.bitgo.impl;

import com.bitso.bitgo.BitGoClient;
import com.bitso.bitgo.SendCoinsResponse;
import com.bitso.bitgo.Wallet;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the BitGo client.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 4:46 PM
 */
public class BitGoClientImpl implements BitGoClient {

    private final Logger log = LoggerFactory.getLogger(getClass());
    /** The base URL (host, port, up to api/v1 for example */
    @Setter @Getter
    private String baseUrl = "http://localhost:3080/api/v1";
    /** The URL for "sendmany" endpoint (must include anything that's appended to baseUrl). */
    @Setter @Getter
    private String sendManyUrl = "/wallet/sendmany";
    @Setter @Getter
    private String listWalletsUrl = "/wallet";
    @Setter @Getter
    private String getWalletUrl = "/wallet/";
    private String longLivedToken;

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
    public Optional<SendCoinsResponse> sendMany(Map<String, BigDecimal> recipients,
                                                String sequenceId, String message,
                                                BigDecimal fee, BigDecimal feeTxConfirmTarget,
                                                int minConfirms, boolean enforceMinConfirmsForChange)
            throws IOException {
        String url = baseUrl + sendManyUrl;
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
            //convert to satoshis
            a.put("amount", e.getValue().movePointRight(8).longValue());
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
            //convert to satoshis
            data.put("fee", fee.movePointRight(8).longValue());
        }
        if (feeTxConfirmTarget != null) {
            data.put("feeTxConfirmTarget", feeTxConfirmTarget);
        }
        if (minConfirms > 0) {
            data.put("minConfirms", minConfirms);
        }
        data.put("enforceMinConfirmsForChange", enforceMinConfirmsForChange);
        Map<String,Object> resp = HttpHelper.readResponse(HttpHelper.post(url, data, auth));
        if (resp.containsKey("error") || resp.containsKey("tx")) {
            SendCoinsResponse r = new SendCoinsResponse();
            r.setTx((String)resp.get("tx"));
            r.setHash((String)resp.get("hash"));
            r.setError((String)resp.get("error"));
            r.setPendingApproval((String)resp.get("pendingApproval"));
            r.setOtp((Boolean)resp.get("otp"));
            r.setTriggeredPolicy((String)resp.get("triggeredPolicy"));
            if (resp.containsKey("status")) {
                r.setStatus((int)resp.get("status"));
            }
            //convert from satoshis
            if (resp.containsKey("fee")) {
                r.setFee(Conversions.satoshiToBitcoin((Long)resp.get("fee")));
            }
            //convert from satoshis
            if (resp.containsKey("feeRate")) {
                r.setFeeRate(Conversions.satoshiToBitcoin((Long)resp.get("feeRate")));
            }
            return Optional.of(r);
        }
        return Optional.empty();
    }

    public List<Wallet> getWallets() throws IOException {
        String url = baseUrl + listWalletsUrl;
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
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> jsw = (List<Map<String,Object>>)resp.get("wallets");
        if (jsw == null) {
            return Collections.emptyList();
        }
        return jsw.stream().map(BitGoClientImpl::fromMap).collect(Collectors.toList());
    }

    public Optional<Wallet> getWallet(String wid) throws IOException {
        String url = baseUrl + getWalletUrl + wid;
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
        if (resp != null && resp.containsKey("id") && resp.containsKey("balance")
                && resp.containsKey("confirmedBalance")) {
            return Optional.of(fromMap(resp));
        }
        return Optional.empty();
    }

    private static Wallet fromMap(Map<String,Object> map) {
        Wallet w = new Wallet();
        w.setId((String)map.get("id"));
        w.setBalance(Conversions.satoshiToBitcoin((long)map.getOrDefault("balance", 0l)));
        w.setConfirmedBalance(Conversions.satoshiToBitcoin((long)map.getOrDefault("confirmedBalance", 0l)));
        return w;
    }
}
