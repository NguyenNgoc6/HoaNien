package com.kawai.controllers.api;

import com.kawai.dto.walkin.WalkInCheckInRequest;
import com.kawai.dto.walkin.WalkInCheckInResponse;
import com.kawai.dto.walkin.WalkInSurchargeResponse;
import com.kawai.services.interfaces.WalkInCheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receptionist/walkin")
public class WalkInCheckInApiController {

    private final WalkInCheckInService walkInCheckInService;

    public WalkInCheckInApiController(WalkInCheckInService walkInCheckInService) {
        this.walkInCheckInService = walkInCheckInService;
    }

    /**
     * API 1: Preview tính toán số lượng khách và số tiền phụ thu
     * Được gọi khi Lễ tân đang nhập ngày sinh của khách hàng
     */
    @PostMapping("/calculate-surcharge")
    public ResponseEntity<WalkInSurchargeResponse> calculateSurchargePreview(
            @RequestBody WalkInCheckInRequest request) {
        WalkInSurchargeResponse response = walkInCheckInService.calculateSurchargePreview(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API 2: Thực hiện luồng Check-in thực tế
     */
    @PostMapping("/checkin")
    public ResponseEntity<WalkInCheckInResponse> walkInCheckIn(
            @RequestBody WalkInCheckInRequest request) {
        WalkInCheckInResponse response = walkInCheckInService.createWalkInBookingAndCheckIn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API 3: Tìm kiếm khách hàng theo SĐT hoặc CCCD
     */
    @GetMapping("/search-customer")
    public ResponseEntity<?> searchCustomer(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "Vui lòng nhập từ khóa tìm kiếm (SĐT hoặc CCCD)"));
        }

        java.util.Optional<com.kawai.models.Customer> customerOpt = walkInCheckInService.searchCustomer(keyword);
        if (customerOpt.isPresent()) {
            com.kawai.models.Customer c = customerOpt.get();
            String cccd = "";
            if (c.getCccdPassportEncrypted() != null) {
                try {
                    cccd = com.kawai.utils.EncryptionUtils.decrypt(c.getCccdPassportEncrypted());
                } catch (Exception e) {
                    cccd = "Lỗi giải mã";
                }
            }
            return ResponseEntity.ok(java.util.Map.of(
                    "id", c.getId(),
                    "fullName", c.getFullName() != null ? c.getFullName() : "",
                    "phone", c.getPhone() != null ? c.getPhone() : "",
                    "email", c.getEmail() != null ? c.getEmail() : "",
                    "cccd", cccd));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(java.util.Map.of("message", "Không tìm thấy khách hàng"));
    }
}
