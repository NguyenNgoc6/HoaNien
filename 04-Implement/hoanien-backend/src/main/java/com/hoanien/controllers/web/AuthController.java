package com.hoanien.controllers.web;

import com.hoanien.models.Account;
import com.hoanien.models.Customer;
import com.hoanien.models.Role;
import com.hoanien.repositories.AccountRepository;
import com.hoanien.repositories.CustomerRepository;
import com.hoanien.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           @RequestParam String fullName,
                           @RequestParam(required = false, defaultValue = "Other") String gender,
                           @RequestParam(required = false, defaultValue = "N/A") String phone,
                           RedirectAttributes redirectAttributes) {
        
        if (accountRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "TÃªn Ä‘Äƒng nháº­p Ä‘Ã£ tá»“n táº¡i!");
            return "redirect:/booking";
        }

        if (customerRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email Ä‘Ã£ tá»“n táº¡i!");
            return "redirect:/booking";
        }

        // Assign default role (CUSTOMER)
        Role customerRole = roleRepository.findByRoleName("CUSTOMER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setRoleName("CUSTOMER");
            return roleRepository.save(newRole);
        });

        // Create Account
        Account account = new Account();
        account.setUsername(username);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setRole(customerRole);
        account = accountRepository.save(account);

        // Create Customer
        Customer customer = new Customer();
        customer.setAccount(account);
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setGender(gender);
        customer.setPhone(phone);
        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("success", "ÄÄƒng kÃ½ thÃ nh cÃ´ng! Vui lÃ²ng Ä‘Äƒng nháº­p.");
        return "redirect:/booking";
    }
}
