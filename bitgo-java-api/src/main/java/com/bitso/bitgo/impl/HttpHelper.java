package com.bitso.bitgo.impl;

import com.alibaba.fastjson.JSON;
import com.bitso.bitgo.BitGoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Helper class for JSON encoding/decoding.
 *
 * @author Enrique Zamudio
 *         Date: 5/8/17 5:51 PM
 */
public class HttpHelper {

    private static final Logger log = LoggerFactory.getLogger(BitGoClient.class);

    public static HttpURLConnection post(String url, Map<String, Object> data,
                                         String auth) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        JSON.writeJSONString(bout, data);

        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)u.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);
        post(conn, auth, bout.toByteArray());
        return conn;
    }

    public static HttpURLConnection postUnsafe(String url, Map<String, Object> data,
                                         String auth) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        JSON.writeJSONString(bout, data);

        URL u = new URL(url);
        HttpURLConnection cc = (HttpURLConnection)u.openConnection();
        cc.setRequestProperty("Content-Type", "application/json");
        cc.setRequestProperty("Authorization", "Bearer " + auth);
        if (cc instanceof HttpsURLConnection) {
            HttpsURLConnection conn = (HttpsURLConnection)cc;
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[] { new TrustyCertManager() }, new java.security.SecureRandom());
                conn.setSSLSocketFactory(sc.getSocketFactory());
                conn.setHostnameVerifier((s, ssl) -> true);
            } catch (GeneralSecurityException ex) {
                return null;
            }
        }
        post(cc, auth, bout.toByteArray());
        return cc;
    }

    private static void post(HttpURLConnection conn, String auth, byte[] buf)
            throws IOException {
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);
        conn.setFixedLengthStreamingMode(buf.length);
        conn.setDoOutput(true);
        conn.getOutputStream().write(buf);
        conn.getOutputStream().flush();
    }

    /** Reads HTTP response (expecting JSON) and returns it as a map. If response
     * is not a map, the data is returned under a map with a single RESPONSE key. */
    public static Map<String,Object> readResponse(HttpURLConnection conn)
            throws IOException {
        int largo = conn.getContentLength();
        byte[] buf = null;
        int lim = 0;
        if (largo > 0) {
            int cuantos = 0;
            buf = new byte[largo];
            while (lim < largo && cuantos >= 0) {
                cuantos = conn.getInputStream().read(buf, lim, buf.length - lim);
                if (cuantos > 0) {
                    lim += cuantos;
                }
            }
        } else if (largo == -1) {
            log.trace("BitGoResp no content-length");
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            buf = new byte[1024];
            while (lim >= 0) {
                lim = conn.getInputStream().read(buf);
                log.trace("BitGoResp read {} bytes", lim);
                if (lim > 0) {
                    bout.write(buf, 0, lim);
                }
            }
            if (bout.size() > 0) {
                lim = bout.size();
                buf = bout.toByteArray();
                log.debug("BitGoResp Total read {} bytes", lim);
            } else {
                buf = null;
            }
        }
        @SuppressWarnings("unchecked")
        Map<String,Object> rmap = (Map<String,Object>)JSON.parse(buf);
        return rmap;
    }

    private static final class TrustyCertManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
