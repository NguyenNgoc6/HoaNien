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

    @Autowired
    private com.kawai.repositories.BookingRepository bookingRepository;

    @GetMapping
    public String showFeedbackPage(
            Principal principal,
            @RequestParam(name = "bookingId", required = false) Long bookingId,
            @RequestParam(name = "type", required = false, defaultValue = "all") String type,
            Model model) {

        boolean isLoggedIn = com.kawai.utils.SecurityUtils.isCustomerLoggedIn(principal);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("activeTab", type);

        com.kawai.models.Customer customer = null;
        boolean hasUsedService = false;

        if (isLoggedIn && principal != null) {
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

            if (customer != null) {
                List<com.kawai.models.Booking> completedBookings = 
                    bookingRepository.findCompletedOrConfirmedBookingsByCustomerId(customer.getId());
                hasUsedService = completedBookings != null && !completedBookings.isEmpty();
            }
        }

        model.addAttribute("hasUsedService", hasUsedService);

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

        // --- 1. Tour Reviews ---
        List<com.kawai.models.Review> tourReviews = new java.util.ArrayList<>();
        
        com.kawai.models.Review mockTour1 = new com.kawai.models.Review();
        mockTour1.setId(-101L);
        com.kawai.models.Customer custTour1 = new com.kawai.models.Customer();
        custTour1.setFullName("Trần Minh Anh");
        mockTour1.setCustomer(custTour1);
        mockTour1.setRatingTour(5);
        mockTour1.setReviewText("Hành trình Thiền hành Yên tử mang lại sự tĩnh lặng sâu sắc. Hướng dẫn viên chu đáo, am hiểu sâu sắc về văn hóa và thiền học.");
        mockTour1.setCreatedAt(java.time.LocalDateTime.of(2026, 6, 15, 9, 0));
        com.kawai.models.TourBooking tbTour1 = new com.kawai.models.TourBooking();
        com.kawai.models.TourSchedule tsTour1 = new com.kawai.models.TourSchedule();
        com.kawai.models.Tour tTour1 = new com.kawai.models.Tour();
        tTour1.setTourName("Hành trình Yên Tử");
        tsTour1.setTour(tTour1);
        tbTour1.setSchedule(tsTour1);
        mockTour1.setTourBooking(tbTour1);
        tourReviews.add(mockTour1);

        com.kawai.models.Review mockTour2 = new com.kawai.models.Review();
        mockTour2.setId(-102L);
        com.kawai.models.Customer custTour2 = new com.kawai.models.Customer();
        custTour2.setFullName("Lê Hoài Nam");
        mockTour2.setCustomer(custTour2);
        mockTour2.setRatingTour(5);
        mockTour2.setReviewText("Một chuyến đi trọn vẹn dọc sông Hậu. Sương sớm trên sông và bữa sáng truyền thống trên ghe máy làm tôi nhớ mãi.");
        mockTour2.setCreatedAt(java.time.LocalDateTime.of(2026, 6, 16, 10, 30));
        com.kawai.models.TourBooking tbTour2 = new com.kawai.models.TourBooking();
        com.kawai.models.TourSchedule tsTour2 = new com.kawai.models.TourSchedule();
        com.kawai.models.Tour tTour2 = new com.kawai.models.Tour();
        tTour2.setTourName("Hành trình Sông Hậu");
        tsTour2.setTour(tTour2);
        tbTour2.setSchedule(tsTour2);
        mockTour2.setTourBooking(tbTour2);
        tourReviews.add(mockTour2);

        List<com.kawai.models.Review> dbTourReviews = safeLoad(() -> reviewRepository.findApprovedTourReviews());
        if (dbTourReviews != null) {
            tourReviews.addAll(dbTourReviews);
        }
        tourReviews.sort((r1, r2) -> {
            java.time.LocalDateTime time1 = r1.getCreatedAt() != null ? r1.getCreatedAt() : java.time.LocalDateTime.MIN;
            java.time.LocalDateTime time2 = r2.getCreatedAt() != null ? r2.getCreatedAt() : java.time.LocalDateTime.MIN;
            return time2.compareTo(time1);
        });

        // --- 2. Room Reviews ---
        List<com.kawai.models.Review> roomReviews = new java.util.ArrayList<>();
        
        com.kawai.models.Review mockRoom1 = new com.kawai.models.Review();
        mockRoom1.setId(-201L);
        com.kawai.models.Customer custRoom1 = new com.kawai.models.Customer();
        custRoom1.setFullName("Phạm Thành Trung");
        mockRoom1.setCustomer(custRoom1);
        mockRoom1.setRatingRoomDining(5);
        mockRoom1.setReviewText("Giường ngủ êm ái, dịch vụ dọn phòng hoàn hảo. Nhà hàng chay phục vụ món ăn thanh đạm nhưng hương vị xuất sắc.");
        mockRoom1.setCreatedAt(java.time.LocalDateTime.of(2026, 6, 29, 14, 0));
        com.kawai.models.TourBooking tbRoom1 = new com.kawai.models.TourBooking();
        com.kawai.models.TourSchedule tsRoom1 = new com.kawai.models.TourSchedule();
        com.kawai.models.Tour tRoom1 = new com.kawai.models.Tour();
        tRoom1.setTourName("Ẩm thực chay HoaNien");
        tsRoom1.setTour(tRoom1);
        tbRoom1.setSchedule(tsRoom1);
        mockRoom1.setTourBooking(tbRoom1);
        roomReviews.add(mockRoom1);

        com.kawai.models.Review mockRoom2 = new com.kawai.models.Review();
        mockRoom2.setId(-202L);
        com.kawai.models.Customer custRoom2 = new com.kawai.models.Customer();
        custRoom2.setFullName("Nguyễn Bích Thủy");
        mockRoom2.setCustomer(custRoom2);
        mockRoom2.setRatingRoomDining(5);
        mockRoom2.setReviewText("Phòng Suite view sông Hậu cực kỳ yên bình. Thiết kế tối giản, tinh tế, gỗ tự nhiên thơm nhẹ rất dễ chịu.");
        mockRoom2.setCreatedAt(java.time.LocalDateTime.of(2026, 6, 20, 15, 0));
        com.kawai.models.TourBooking tbRoom2 = new com.kawai.models.TourBooking();
        com.kawai.models.TourSchedule tsRoom2 = new com.kawai.models.TourSchedule();
        com.kawai.models.Tour tRoom2 = new com.kawai.models.Tour();
        tRoom2.setTourName("Lưu trú HoaNien");
        tsRoom2.setTour(tRoom2);
        tbRoom2.setSchedule(tsRoom2);
        mockRoom2.setTourBooking(tbRoom2);
        roomReviews.add(mockRoom2);

        List<com.kawai.models.Review> dbRoomReviews = safeLoad(() -> reviewRepository.findApprovedRoomReviews());
        if (dbRoomReviews != null) {
            roomReviews.addAll(dbRoomReviews);
        }
        roomReviews.sort((r1, r2) -> {
            java.time.LocalDateTime time1 = r1.getCreatedAt() != null ? r1.getCreatedAt() : java.time.LocalDateTime.MIN;
            java.time.LocalDateTime time2 = r2.getCreatedAt() != null ? r2.getCreatedAt() : java.time.LocalDateTime.MIN;
            return time2.compareTo(time1);
        });

        // --- 3. General Reviews ---
        List<com.kawai.models.Review> generalReviews = new java.util.ArrayList<>();
        
        com.kawai.models.Review mockGen1 = new com.kawai.models.Review();
        mockGen1.setId(-301L);
        com.kawai.models.Customer custGen1 = new com.kawai.models.Customer();
        custGen1.setFullName("Đỗ Quốc Huy");
        mockGen1.setCustomer(custGen1);
        mockGen1.setRatingService(5);
        mockGen1.setReviewText("Dịch vụ đưa đón tận tình, nhân viên thân thiện. Không gian Hub kết nối tuyệt vời để thư giãn đọc sách.");
        mockGen1.setCreatedAt(java.time.LocalDateTime.of(2026, 7, 5, 11, 20));
        com.kawai.models.TourBooking tbGen1 = new com.kawai.models.TourBooking();
        com.kawai.models.TourSchedule tsGen1 = new com.kawai.models.TourSchedule();
        com.kawai.models.Tour tGen1 = new com.kawai.models.Tour();
        tGen1.setTourName("Dịch vụ đưa đón");
        tsGen1.setTour(tGen1);
        tbGen1.setSchedule(tsGen1);
        mockGen1.setTourBooking(tbGen1);
        generalReviews.add(mockGen1);

        com.kawai.models.Review mockGen2 = new com.kawai.models.Review();
        mockGen2.setId(-302L);
        com.kawai.models.Customer custGen2 = new com.kawai.models.Customer();
        custGen2.setFullName("Hoàng Thu Thảo");
        mockGen2.setCustomer(custGen2);
        mockGen2.setRatingService(5);
        mockGen2.setReviewText("Trải nghiệm Spa trị liệu bằng thảo mộc tự nhiên tại HoaNien thực sự giúp tôi phục hồi năng lượng sau những ngày căng thẳng.");
        mockGen2.setCreatedAt(java.time.LocalDateTime.of(2026, 6, 18, 16, 0));
        com.kawai.models.TourBooking tbGen2 = new com.kawai.models.TourBooking();
        com.kawai.models.TourSchedule tsGen2 = new com.kawai.models.TourSchedule();
        com.kawai.models.Tour tGen2 = new com.kawai.models.Tour();
        tGen2.setTourName("Thảo mộc Spa");
        tsGen2.setTour(tGen2);
        tbGen2.setSchedule(tsGen2);
        mockGen2.setTourBooking(tbGen2);
        generalReviews.add(mockGen2);

        List<com.kawai.models.Review> dbGenReviews = safeLoad(() -> reviewRepository.findApprovedGeneralReviews());
        if (dbGenReviews != null) {
            generalReviews.addAll(dbGenReviews);
        }
        generalReviews.sort((r1, r2) -> {
            java.time.LocalDateTime time1 = r1.getCreatedAt() != null ? r1.getCreatedAt() : java.time.LocalDateTime.MIN;
            java.time.LocalDateTime time2 = r2.getCreatedAt() != null ? r2.getCreatedAt() : java.time.LocalDateTime.MIN;
            return time2.compareTo(time1);
        });

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

        if (!com.kawai.utils.SecurityUtils.isCustomerLoggedIn(principal)) {
            return "redirect:/feedback?toast=login_required";
        }

        com.kawai.models.Customer customer = null;
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

        if (customer == null) {
            return "redirect:/feedback?toast=error";
        }

        // Check if customer has used any services
        List<com.kawai.models.Booking> completedBookings = 
            bookingRepository.findCompletedOrConfirmedBookingsByCustomerId(customer.getId());
        if (completedBookings == null || completedBookings.isEmpty()) {
            return "redirect:/feedback?toast=no_service_used";
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
