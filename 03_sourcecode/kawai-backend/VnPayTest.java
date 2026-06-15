import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnPayTest {

    static String SECRET = "XPZUP5CYBQGK8NEYSI2WLI77BAIS27X6";
    static String TMN = "9G3DZZD2";

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
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

        List<String> parts = new ArrayList<>();
        for (String k : keys) {
            String v = sign.get(k);
            if (v != null && !v.isEmpty()) {
                String enc = URLEncoder.encode(v, "UTF-8").replace("+", "%20");
                parts.add(k + "=" + enc);
            }
        }

        String hashData = String.join("&", parts);
        String hash = hmacSHA512(SECRET, hashData);

        System.out.println("HashData:");
        System.out.println(hashData);
        System.out.println();
        System.out.println("Hash: " + hash);
    }
}
