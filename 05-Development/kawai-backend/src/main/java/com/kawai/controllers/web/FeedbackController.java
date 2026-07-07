package com.kawai.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private com.kawai.repositories.CustomerRepository customerRepository;

    @Autowired
    private com.kawai.repositories.TourBookingRepository tourBookingRepository;

    @Autowired
    private com.kawai.repositories.ReviewRepository reviewRepository;

    @GetMapping
    public String showFeedbackPage(
            Principal principal,
            @RequestParam(name = "bookingId", required = false) Long bookingId,
            @RequestParam(name = "type", required = false, defaultValue = "all") String type,
            Model model) {

        model.addAttribute("isLoggedIn", com.kawai.utils.SecurityUtils.isCustomerLoggedIn(principal));
        model.addAttribute("activeTab", type);

        // Mode: submit form (came from email with bookingId)
        com.kawai.models.TourBooking booking = null;
        if (bookingId != null) {
            booking = tourBookingRepository.findById(bookingId).orElse(null);
        }

        if (booking != null) {
            model.addAttribute("booking", booking);
            model.addAttribute("tourName", booking.getSchedule() != null && booking.getSchedule().getTour() != null
                    ? booking.getSchedule().getTour().getTourName() : "Hành trình trải nghiệm");
            model.addAttribute("mode", "submit");
        } else {
            model.addAttribute("booking", null);
            model.addAttribute("tourName", null);
            model.addAttribute("mode", "browse");
        }

        // Load reviews by tab/category
        List<com.kawai.models.Review> tourReviews = safeLoad(() -> reviewRepository.findApprovedTourReviews());
        List<com.kawai.models.Review> roomReviews = safeLoad(() -> reviewRepository.findApprovedRoomReviews());
        List<com.kawai.models.Review> generalReviews = safeLoad(() -> reviewRepository.findApprovedGeneralReviews());

        model.addAttribute("tourReviews", tourReviews);
        model.addAttribute("roomReviews", roomReviews);
        model.addAttribute("generalReviews", generalReviews);

        return "guest/feedback";
    }

    @PostMapping("/submit")
    public String submitFeedback(
            Principal principal,
            @RequestParam(name = "bookingId", required = false) Long bookingId,
            @RequestParam("ratingTour") Integer ratingTour,
            @RequestParam("ratingRoomDining") Integer ratingRoomDining,
            @RequestParam("reviewText") String reviewText) {

        com.kawai.models.Customer customer = null;
        if (com.kawai.utils.SecurityUtils.isCustomerLoggedIn(principal)) {
            String username = principal.getName();
            if (principal instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
                org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauthToken =
                    (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) principal;
                username = oauthToken.getPrincipal().getAttribute("email");
            }
            final String effectiveUsername = username;
            customer = customerRepository.findByAccount_Username(effectiveUsername)
                    .or(() -> customerRepository.findByEmail(effectiveUsername))
                    .orElse(null);
        }

        // Fallback: customer #5 for demo
        if (customer == null) {
            customer = customerRepository.findById(5L).orElse(null);
        }

        if (customer == null) {
            return "redirect:/feedback?toast=error";
        }

        com.kawai.models.TourBooking booking = null;
        if (bookingId != null) {
            booking = tourBookingRepository.findById(bookingId).orElse(null);
        }

        com.kawai.models.Review review = new com.kawai.models.Review();
        review.setCustomer(customer);
        review.setTourBooking(booking);
        review.setRatingTour(ratingTour);
        review.setRatingRoomDining(ratingRoomDining);
        review.setRatingService((ratingTour + ratingRoomDining) / 2);
        review.setReviewText(reviewText);
        review.setCreatedAt(LocalDateTime.now());
        review.setModerationStatus("Approved"); // Auto-approved for demo

        reviewRepository.save(review);

        return "redirect:/feedback?toast=success&type=all";
    }

    private <T> List<T> safeLoad(java.util.function.Supplier<List<T>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
