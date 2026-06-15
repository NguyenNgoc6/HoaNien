package com.kawai.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class BookingController {

    @GetMapping({ "/", "/booking" })
    public String showBookingPage(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);

        return "guest/booking";
    }

    @GetMapping("/living")
    public String showLivingPage(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        return "guest/living";
    }

    @GetMapping("/wellbeing")
    public String showWellbeingPage(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        return "guest/wellbeing";
    }

    @GetMapping("/dining")
    public String showDiningPage(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        return "guest/dining";
    }

    @GetMapping("/experiences")
    public String showExperiencesPage(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        return "guest/experiences";
    }

    // Inner class BookingRoom removed
}
