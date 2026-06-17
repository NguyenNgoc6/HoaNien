package com.kawai.controllers.web;

import com.kawai.models.Account;
import com.kawai.models.Customer;
import com.kawai.repositories.AccountRepository;
import com.kawai.repositories.CustomerRepository;
import com.kawai.models.RoomBooking;
import com.kawai.models.RoomBookingDetail;
import com.kawai.repositories.RoomBookingRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private RoomBookingDetailRepository roomBookingDetailRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/booking"; // Guest login page
        }
        String username = authentication.getName();
        Customer customer = customerRepository.findByAccount_Username(username).orElse(null);
        model.addAttribute("customer", customer);

        return "guest/profile";
    }

    @GetMapping("/bookings")
    public String viewBookingHistory(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/booking";
        }
        String username = authentication.getName();
        Customer customer = customerRepository.findByAccount_Username(username).orElse(null);
        model.addAttribute("customer", customer);

        if (customer != null) {
            List<RoomBooking> bookings = roomBookingRepository.findByCustomerOrderByBookingDateDesc(customer)
                    .stream()
                    .filter(b -> !"HOLD".equals(b.getBookingStatus()))
                    .collect(java.util.stream.Collectors.toList());
            Map<Long, RoomBookingDetail> firstDetails = new HashMap<>();
            
            for (RoomBooking booking : bookings) {
                List<RoomBookingDetail> details = roomBookingDetailRepository.findByRoomBookingId(booking.getId());
                if (!details.isEmpty()) {
                    firstDetails.put(booking.getId(), details.get(0));
                }
            }
            model.addAttribute("bookings", bookings);
            model.addAttribute("bookingFirstDetails", firstDetails);
        }

        return "guest/booking-history";
    }

    @PostMapping("/update")
    public String editProfile(Authentication authentication,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String cccd,
            @RequestParam String gender,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Customer customer = customerRepository.findByAccount_Username(username).orElse(null);
        if (customer != null) {
            customer.setFullName(fullName);
            customer.setPhone(phone);
            customer.setGender(gender != null ? gender.toUpperCase() : "MALE");
            if (cccd != null && !cccd.equals("********") && !cccd.trim().isEmpty()) {
                customer.setCccdPassportEncrypted(cccd);
            }
            if (email != null && !email.trim().isEmpty()) {
                customer.setEmail(email);
            }
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication authentication,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username).orElse(null);

        if (account != null) {
            if (passwordEncoder.matches(oldPassword, account.getPasswordHash())) {
                account.setPasswordHash(passwordEncoder.encode(newPassword));
                accountRepository.save(account);
                redirectAttributes.addFlashAttribute("success", "Äá»•i máº­t kháº©u thÃ nh cÃ´ng!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Máº­t kháº©u cÅ© khÃ´ng chÃ­nh xÃ¡c!");
            }
        }
        return "redirect:/profile";
    }
}
