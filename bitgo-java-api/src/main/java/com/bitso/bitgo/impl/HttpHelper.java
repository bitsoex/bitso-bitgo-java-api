package com.bitso.bitgo.impl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Helper class for JSON encoding/decoding.
 *
 * @author Enrique Zamudio
 * Date: 5/8/17 5:51 PM
 */
@Slf4j
public class HttpHelper {


    public static HttpURLConnection get(String url,
                                        String auth, Map<String, String> reqParams) throws IOException {
        HttpURLConnection conn = createConn(url, auth, reqParams);
        return conn;
    }


    public static HttpURLConnection getUnsafe(String url,
                                              String auth, Map<String, String> reqParams) throws IOException {
        HttpURLConnection conn = createConn(url, auth, reqParams);
        unsafeConnection(conn);
        return conn;
    }


    public static HttpURLConnection post(String url, Map<String, Object> data,
                                         String auth) throws IOException {
        HttpURLConnection conn = createConn(url, auth, null);
        post(conn, auth, data);
        return conn;
    }

    public static HttpURLConnection postUnsafe(String url, Map<String, Object> data,
                                               String auth) throws IOException {
        HttpURLConnection conn = createConn(url, auth, null);
        unsafeConnection(conn);
        post(conn, auth, data);
        return conn;
    }

    private static HttpURLConnection createConn(String szUrl, String auth, Map<String, String> reqParams) throws IOException {
        URL url = new URL(addQueryStringToUrlString(szUrl, reqParams));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + auth);

        if (reqParams != null) {
            for (Map.Entry<String, String> entry : reqParams.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    //TODO look into using https://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html
    private static String addQueryStringToUrlString(String url, final Map<String, String> parameters) throws UnsupportedEncodingException {
        if (parameters == null) {
            return url;
        }

        for (Map.Entry<String, String> parameter : parameters.entrySet()) {

            final String encodedKey = URLEncoder.encode(parameter.getKey(), "UTF-8");
            final String encodedValue = URLEncoder.encode(parameter.getValue(), "UTF-8");

            if (!url.contains("?")) {  //slow if looping
                url += "?" + encodedKey + "=" + encodedValue;
            } else {
                url += "&" + encodedKey + "=" + encodedValue;
            }
        }

        return url;
    }

    private static boolean unsafeConnection(HttpURLConnection cc) {
        if (cc instanceof HttpsURLConnection) {
            HttpsURLConnection conn = (HttpsURLConnection) cc;
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new TrustyCertManager()}, new java.security.SecureRandom());
                conn.setSSLSocketFactory(sc.getSocketFactory());
                conn.setHostnameVerifier((s, ssl) -> true);
            } catch (GeneralSecurityException ex) {
                return true;
            }
        }
        return false;
    }

    private static void post(HttpURLConnection conn, String auth, Map<String, Object> data)
            throws IOException {

        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Content-Type", "application/json");
//        conn.setRequestProperty("Authorization", "Bearer " + auth);

        final byte[] buf = SerializationUtil.mapper.writeValueAsBytes(data);
        conn.setFixedLengthStreamingMode(buf.length);
        conn.setDoOutput(true);
        conn.getOutputStream().write(buf);
        conn.getOutputStream().flush();

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
