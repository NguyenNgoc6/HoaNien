package com.kawai.controllers;

import com.kawai.services.interfaces.VnPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentApiController {

    @Autowired
    private VnPayService vnPayService;

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> queryParams) {
        return ResponseEntity.ok(queryParams);
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(@RequestParam Map<String, String> queryParams) {
        Map<String, String> result = vnPayService.verifyIpn(queryParams);
        return ResponseEntity.ok(result);
    }
}
