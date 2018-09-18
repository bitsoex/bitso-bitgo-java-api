package com.bitso.bitgo.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * @author kushal256
 * Date: 9/14/18
 */
@Slf4j
public class DataScrub {

    @Setter @Getter
    private static boolean HASH = false;

    @Setter @Getter
    private static byte[] nonce = UUID.randomUUID().toString().getBytes();
    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            log.error("Error", e);
        }
    }

    public static class StringSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
            if (!HASH) {
                jgen.writeString(value);
            } else {
                DataScrub.digest.update(nonce);  //note:  digest is not thread safe, so update and digest should be in synchronized block if we want to
                byte[] digest = DataScrub.digest.digest(value.getBytes());
                jgen.writeBinary(Base64.getEncoder().encode(digest));
            }
        }
    }

    /**
     * https://www.bitgo.com/api/v2/#get-wallet-transaction the Output.id field is displayed as:
     * "cc21875eb303e5efda9056585d68c79b10345585213a62c9c1a7bc331dfd5d93:0" so we want to just hash the part before the colon, the part after is the vout
     * <p>
     * example result would be:
     * <p>
     * "+xXrhY+Ci2u+dUvW+ve0WhYqI5mb/Hm7orSUNbhh57k=:0"
     */
    public static class HashColonSerializer extends JsonSerializer<String> {
        private static final char COLON = ':';

        @Override
        public void serialize(String value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
            if (!HASH) {
                jgen.writeString(value);
            } else {
                String txId = null;
                String suffix = null;
                int colonIndex = value.lastIndexOf(COLON);
                if (colonIndex != -1) {
                    txId = value.substring(0, colonIndex);
                    suffix = value.substring(colonIndex);
                } else {
                    txId = value;
                }
                DataScrub.digest.update(nonce);  //note:  digest is not thread safe, so update and digest should be in synchronized block if we want to
                String base64 = Base64.getEncoder().encodeToString(digest.digest(txId.getBytes()));
                if (suffix != null) {
                    base64 += suffix;
                }
                jgen.writeString(base64);
            }
        }
    }

}
