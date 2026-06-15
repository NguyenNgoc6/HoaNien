package com.kawai.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnPayUtil {

    private static final boolean DEBUG = true;

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException("VNPay HMAC-SHA512 hash generation failed", ex);
        }
    }

    public static String getPaymentUrl(String baseUrl, Map<String, String> fields, String secretKey) {
        // Exclude signature fields from hash calculation (VNPay spec)
        Map<String, String> signFields = new HashMap<>(fields);
        signFields.remove("vnp_SecureHash");
        signFields.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(signFields.keySet());
        Collections.sort(fieldNames);

        List<String> queryParts = new ArrayList<>();
        List<String> hashParts = new ArrayList<>();

        for (String fieldName : fieldNames) {
            String fieldValue = signFields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    String encodedName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString())
                            .replace("+", "%20");
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())
                            .replace("+", "%20");

                    hashParts.add(fieldName + "=" + encodedValue);
                    queryParts.add(encodedName + "=" + encodedValue);
                } catch (UnsupportedEncodingException e) {
                    hashParts.add(fieldName + "=" + fieldValue);
                    queryParts.add(fieldName + "=" + fieldValue);
                }
            }
        }

        String queryUrl = String.join("&", queryParts);
        String hashData = String.join("&", hashParts);
        String vnp_SecureHash = hmacSHA512(secretKey, hashData);

        if (DEBUG) {
            System.out.println("===== VNPay DEBUG =====");
            System.out.println("HashData (before HMAC):");
            System.out.println(hashData);
            System.out.println("---");
            System.out.println("Secret Key: " + secretKey);
            System.out.println("Computed Hash: " + vnp_SecureHash);
            System.out.println("=======================");
        }

        // Append signature fields AFTER hash calculation (not part of hash)
        return baseUrl + "?" + queryUrl
                + "&vnp_SecureHashType=SHA512"
                + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    public static boolean validateCallback(Map<String, String> fields, String secureHash, String secretKey) {
        Map<String, String> signFields = new HashMap<>(fields);
        signFields.remove("vnp_SecureHash");
        signFields.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(signFields.keySet());
        Collections.sort(fieldNames);

        List<String> hashParts = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String fieldValue = signFields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                try {
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())
                            .replace("+", "%20");
                    hashParts.add(fieldName + "=" + encodedValue);
                } catch (UnsupportedEncodingException e) {
                    hashParts.add(fieldName + "=" + fieldValue);
                }
            }
        }

        String hashData = String.join("&", hashParts);
        String computedHash = hmacSHA512(secretKey, hashData);
        return computedHash.equalsIgnoreCase(secureHash);
    }
}
