package com.kawai.controllers.api;
import com.kawai.services.interfaces.EmailService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/test-email")
public class TestEmailController {
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/otp")
    public String testOtp() {
        try {
            emailService.sendRegistrationOtpEmail("test@test.com", "123456", "Test User");
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString() + " - " + e.getMessage();
        }
    }
}
