package com.kawai.controllers;

import com.kawai.dto.BookingRequestDTO;
import com.kawai.dto.BookingResponseDTO;
import com.kawai.dto.BookingApiResponse;
import com.kawai.dto.BookingDetailResponseDTO;
import com.kawai.models.Customer;
import com.kawai.repositories.CustomerRepository;
import com.kawai.services.interfaces.BookingService;
import com.kawai.exceptions.BusinessException;
import com.kawai.models.RoomBooking;
import com.kawai.repositories.RoomBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private static final Logger log = LoggerFactory.getLogger(BookingApiController.class);

    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.api-url}")
    private String vnp_ApiUrl;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @PostMapping
    public ResponseEntity<?> createBooking(Principal principal, @RequestBody BookingRequestDTO request) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new BookingApiResponse(
                    "error", null, null, null, "Quý khách cần đăng nhập để thực hiện đặt phòng!"));
        }

        try {
            String username = principal.getName();
            Customer customer = customerRepository.findByAccount_Username(username)
                    .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND",
                            "Không tìm thấy thông tin khách hàng cho tài khoản: " + username));

            // Gán customerId lấy từ user đang đăng nhập
            request.setCustomerId(customer.getId());

            BookingResponseDTO response = bookingService.createBooking(request);

            java.time.LocalDateTime cancellationDeadlineLDT = response.getCancellationDeadline() != null
                    ? response.getCancellationDeadline().atStartOfDay()
                    : null;

            BookingApiResponse apiResponse = new BookingApiResponse(
                    "success",
                    response.getBookingStatus(),
                    response.getBookingId(),
                    response.getDepositAmount(),
                    cancellationDeadlineLDT,
                    "Đặt phòng thành công!");
            // ✅ Trả về tổng chính thức từ backend (bao gồm phụ thu + khuyến mãi)
            apiResponse.setDiscountedPrice(response.getDiscountedPrice());

            return ResponseEntity.ok(apiResponse);
        } catch (BusinessException e) {
            return ResponseEntity.status(400).body(new BookingApiResponse(
                    "error", null, null, null, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(new BookingApiResponse(
                    "error", null, null, null,
                    e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi không xác định"));
        }
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(Principal principal, @PathVariable Long bookingId) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new BookingApiResponse(
                    "error", null, null, null, "Quý khách cần đăng nhập để thực hiện thao tác này!"));
        }

        try {
            String username = principal.getName();
            Customer customer = customerRepository.findByAccount_Username(username)
                    .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND",
                            "Không tìm thấy thông tin khách hàng cho tài khoản: " + username));

            BookingResponseDTO response = bookingService.cancelBooking(bookingId, customer.getId());

            return ResponseEntity.ok(new BookingApiResponse(
                    "success",
                    response.getBookingStatus(),
                    response.getBookingId(),
                    response.getDepositAmount(),
                    null,
                    "Hủy phòng thành công!"));
        } catch (com.kawai.exceptions.PaymentGatewayException e) {
            return ResponseEntity.status(500).body(new BookingApiResponse(
                    "error", null, null, null, e.getMessage()));
        } catch (BusinessException e) {
            return ResponseEntity.status(400).body(new BookingApiResponse(
                    "error", null, null, null, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new BookingApiResponse(
                    "error", null, null, null,
                    e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi không xác định"));
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingDetail(Principal principal, @PathVariable Long bookingId) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Quý khách cần đăng nhập để thực hiện thao tác này!"));
        }
        try {
            String username = principal.getName();
            Customer customer = customerRepository.findByAccount_Username(username)
                    .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "Không tìm thấy thông tin khách hàng cho tài khoản: " + username));

            BookingDetailResponseDTO detail = bookingService.getBookingDetail(bookingId, customer.getId());
            return ResponseEntity.ok(detail);
        } catch (BusinessException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi"));
        }
    }

    @PostMapping("/{bookingId}/apply-coupon")
    public ResponseEntity<?> applyCoupon(Principal principal, @PathVariable Long bookingId, @RequestBody Map<String, String> payload) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Quý khách cần đăng nhập!"));
        }
        try {
            String username = principal.getName();
            Customer customer = customerRepository.findByAccount_Username(username)
                    .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "Không tìm thấy thông tin khách hàng"));

            String couponCode = payload.get("couponCode");
            BigDecimal discountAmount = bookingService.applyCoupon(bookingId, couponCode, customer.getId());

            return ResponseEntity.ok(Map.of("status", "success", "discountAmount", discountAmount));
        } catch (BusinessException e) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi"));
        }
    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(
            Principal principal,
            @PathVariable Long bookingId,
            @RequestBody Map<String, Object> payload,
            jakarta.servlet.http.HttpServletRequest request) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Quý khách cần đăng nhập!"));
        }
        try {
            String username = principal.getName();
            Customer customer = customerRepository.findByAccount_Username(username)
                    .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "Không tìm thấy thông tin khách hàng"));

            String fullName = (String) payload.get("fullName");
            String phone = (String) payload.get("phone");
            String email = (String) payload.get("email");
            String cccd = (String) payload.get("cccd");
            String notes = (String) payload.get("notes");

            bookingService.confirmBooking(bookingId, customer.getId(), fullName, phone, email, cccd, notes);

            // Generate VNPay payment URL for the 30% deposit
            RoomBooking booking = roomBookingRepository.findById(bookingId)
                    .orElseThrow(() -> new BusinessException("BKG-003", "Không tìm thấy đơn đặt phòng với ID: " + bookingId));

            // VNPAY expects amount in cents/sub-units (VND * 100)
            BigDecimal amount = booking.getDepositAmount().multiply(new BigDecimal("100"));
            String vnp_AmountStr = String.valueOf(amount.setScale(0, java.math.RoundingMode.HALF_UP));

            String vnp_TxnRef = bookingId + "_" + System.currentTimeMillis();
            String vnp_OrderInfo = "Thanh toan dat coc dat phong " + bookingId;

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String vnp_CreateDate = java.time.LocalDateTime.now().format(formatter);

            // Determine IP address
            String vnp_IpAddr = "127.0.0.1";
            if (request != null) {
                String remoteAddr = request.getHeader("X-FORWARDED-FOR");
                if (remoteAddr == null || remoteAddr.isEmpty()) {
                    remoteAddr = request.getRemoteAddr();
                }
                if (remoteAddr != null) {
                    vnp_IpAddr = remoteAddr.split(",")[0].trim();
                }
            }
            if (vnp_IpAddr == null || vnp_IpAddr.contains(":") || vnp_IpAddr.equals("0:0:0:0:0:0:0:1")) {
                vnp_IpAddr = "127.0.0.1";
            }

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", vnp_AmountStr);
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", "250000"); // Hotel & Accommodation
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            String paymentUrl = com.kawai.utils.VnPayUtil.getPaymentUrl(vnp_ApiUrl, vnp_Params, vnp_HashSecret);

            return ResponseEntity.ok(Map.of("status", "success", "paymentUrl", paymentUrl));
        } catch (BusinessException e) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi"));
        }
    }

    @GetMapping("/vnpay-callback")
    public void vnpayCallback(
            @RequestParam Map<String, String> queryParams,
            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        log.info("VNPAY Callback received: {}", queryParams);

        String secureHash = queryParams.get("vnp_SecureHash");
        if (secureHash == null) {
            log.error("VNPAY Callback: missing secure hash");
            response.sendRedirect("/booking?error=invalid_signature");
            return;
        }

        boolean isValid = com.kawai.utils.VnPayUtil.validateCallback(queryParams, secureHash, vnp_HashSecret);
        if (!isValid) {
            log.error("VNPAY Callback: signature verification failed!");
            response.sendRedirect("/booking?error=invalid_signature");
            return;
        }

        String responseCode = queryParams.get("vnp_ResponseCode");
        String txnRef = queryParams.get("vnp_TxnRef");

        if (txnRef == null || txnRef.isEmpty()) {
            log.error("VNPAY Callback: missing txnRef");
            response.sendRedirect("/booking?error=missing_txnref");
            return;
        }

        String bookingIdStr = txnRef.split("_")[0];
        Long bookingId = Long.parseLong(bookingIdStr);

        if ("00".equals(responseCode)) {
            try {
                bookingService.completePayment(bookingId);
                response.sendRedirect("/profile?success=payment");
            } catch (Exception e) {
                log.error("Error completing payment for booking " + bookingId, e);
                response.sendRedirect("/payment?bookingId=" + bookingId + "&error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
            }
        } else {
            log.warn("VNPAY Callback: payment failed with response code {}", responseCode);
            response.sendRedirect("/payment?bookingId=" + bookingId + "&error=payment_failed");
        }
    }
}
