import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class VnPayTest2 {
    static String SECRET = "XPZUP5CYBQGK8NEYSI2WLI77BAIS27X6";
    static String TMN = "9G3DZZD2";

    static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] r = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : r) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception e) { return "ERROR: " + e.getMessage(); }
    }

    static String sha256Hex(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] r = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : r) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception e) { return "ERROR"; }
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", TMN);
        params.put("vnp_Amount", "200000000");
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", "100_1765785600000");
        params.put("vnp_OrderInfo", "Thanh toan dat coc dat phong 100");
        params.put("vnp_OrderType", "250000");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", "http://localhost:8080/api/bookings/vnpay-callback");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", "20260615143000");

        Map<String, String> sign = new HashMap<>(params);
        sign.remove("vnp_SecureHash");
        sign.remove("vnp_SecureHashType");
        List<String> keys = new ArrayList<>(sign.keySet());
        Collections.sort(keys);

        // === Method 1: URL-encoded values (current code) ===
        List<String> parts1 = new ArrayList<>();
        for (String k : keys) {
            String v = sign.get(k);
            if (v != null && !v.isEmpty())
                parts1.add(k + "=" + URLEncoder.encode(v, "UTF-8").replace("+", "%20"));
        }
        String hd1 = String.join("&", parts1);
        System.out.println("=== Method 1: URL-encoded values, HMAC-SHA512 ===");
        System.out.println("Hash: " + hmacSHA512(SECRET, hd1));

        // === Method 2: RAW values (no encoding), HMAC-SHA512 ===
        List<String> parts2 = new ArrayList<>();
        for (String k : keys) {
            String v = sign.get(k);
            if (v != null && !v.isEmpty())
                parts2.add(k + "=" + v);
        }
        String hd2 = String.join("&", parts2);
        System.out.println("=== Method 2: RAW values, HMAC-SHA512 ===");
        System.out.println("Hash: " + hmacSHA512(SECRET, hd2));

        // === Method 3: URL-encoded, but SHA-256 (plain, no HMAC) ===
        System.out.println("=== Method 3: URL-encoded values, SHA-256 (plain) ===");
        System.out.println("Hash: " + sha256Hex(hd1));

        // === Method 4: secretKey + hashData (old VNPay style) ===
        System.out.println("=== Method 4: secretKey + hashData, HMAC-SHA512 ===");
        System.out.println("Hash: " + hmacSHA512(SECRET, SECRET + hd1));

        System.out.println();
        System.out.println("Compare ALL 4 on VNPay demo page!");
    }
}
