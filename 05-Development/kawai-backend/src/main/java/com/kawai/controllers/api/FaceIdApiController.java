package com.kawai.controllers.api;

import com.kawai.models.TourAttendee;
import com.kawai.models.TourSchedule;
import com.kawai.models.Employee;
import com.kawai.repositories.TourAttendeeRepository;
import com.kawai.repositories.EmployeeRepository;
import com.kawai.repositories.TourScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * REST API cho chức năng AI Face Scan (UC21).
 * Nhận kết quả từ trình duyệt (face-api.js) và cập nhật CSDL.
 */
@RestController
@RequestMapping("/api/faceid")
public class FaceIdApiController {

    @Autowired
    private TourAttendeeRepository tourAttendeeRepository;

    @Autowired
    private com.kawai.repositories.CustomerRepository customerRepository;

    @Autowired
    private com.kawai.repositories.DependentRepository dependentRepository;

    @Autowired
    private com.kawai.repositories.BookingRepository bookingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TourScheduleRepository tourScheduleRepository;

    /**
     * POST /api/faceid/verify
     * Nhận kết quả nhận diện từ JavaScript (face-api.js) trên trình duyệt.
     * Frontend đã tự xử lý nhận diện và gửi tên khách khớp về server.
     * Request: { "name": "Lê Hoàng Nam" }
     */
    @PostMapping("/verify")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> verifyFromBrowser(@RequestBody Map<String, String> body, Principal principal) {
        String matchedName = body.get("name");
        if (matchedName == null || matchedName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Thiếu tên khách hàng"));
        }

        String displayName = matchedName;

        try {
            List<TourAttendee> attendees;
            if (principal != null) {
                String username = principal.getName();
                Employee emp = employeeRepository.findByAccountUsername(username).orElse(null);
                if (emp != null) {
                    List<TourSchedule> daySchedules = tourScheduleRepository.findByDepartureDate(LocalDate.now());
                    TourSchedule guideSchedule = null;
                    for (TourSchedule s : daySchedules) {
                        if (emp.getFullName().equalsIgnoreCase(getGuideForSchedule(s))) {
                            guideSchedule = s;
                            break;
                        }
                    }
                    if (guideSchedule != null) {
                        attendees = tourAttendeeRepository.findByTourBooking_Schedule_Id(guideSchedule.getId());
                    } else {
                        attendees = Collections.emptyList();
                    }
                } else {
                    attendees = tourAttendeeRepository.findByTourBooking_Schedule_DepartureDate(LocalDate.now());
                }
            } else {
                attendees = tourAttendeeRepository.findByTourBooking_Schedule_DepartureDate(LocalDate.now());
            }

            for (TourAttendee attendee : attendees) {
                String name = null;
                if (attendee.getCustomer() != null) {
                    name = attendee.getCustomer().getFullName();
                } else if (attendee.getDependent() != null) {
                    name = attendee.getDependent().getDependentName();
                }

                if (name != null && isNameMatch(name, displayName)) {
                    if (!"Checked_In".equals(attendee.getStatus())) {
                        attendee.setStatus("Checked_In");
                        attendee.setFaceMatchedAt(LocalDateTime.now());
                        tourAttendeeRepository.saveAndFlush(attendee);
                    }
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "name", displayName,
                            "attendeeId", String.valueOf(attendee.getId()),
                            "time", LocalDateTime.now().toString()));
                }
            }

            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Không tìm thấy khách '" + displayName + "' trong danh sách tour của bạn hôm nay"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Lỗi máy chủ: " + e.getMessage()));
        }
    }

    /**
     * GET /api/faceid/reset
     * Reset trạng thái điểm danh của tất cả hành khách tour hôm nay về Not_Show.
     * Dùng cho demo replay.
     */
    @GetMapping("/reset")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> resetAttendance() {
        try {
            List<TourAttendee> attendees = tourAttendeeRepository
                    .findByTourBooking_Schedule_DepartureDate(LocalDate.now());
            int resetCount = 0;
            for (TourAttendee attendee : attendees) {
                if (!"Not_Show".equals(attendee.getStatus())) {
                    attendee.setStatus("Not_Show");
                    attendee.setFaceMatchedAt(null);
                    tourAttendeeRepository.saveAndFlush(attendee);
                    resetCount++;
                }
            }
            return ResponseEntity.ok(Map.of("success", true, "resetCount", resetCount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * POST /api/faceid/enroll
     * Thu thập dữ liệu khuôn mặt (đăng ký) cho khách hàng (Customer hoặc Dependent)
     * Request: { "customerId": 1, "dependentId": null, "faceVectorData": "[...]" }
     */
    @PostMapping("/enroll")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> enrollFaceId(@RequestBody Map<String, Object> body) {
        try {
            String faceVectorData = (String) body.get("faceVectorData");
            if (faceVectorData == null || faceVectorData.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Thiếu dữ liệu nhận diện khuôn mặt"));
            }

            String base64Image = (String) body.get("faceImageBase64");
            String savedImageUrl = null;
            if (base64Image != null && base64Image.contains(",")) {
                try {
                    String base64Data = base64Image.split(",")[1];
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                    String dirPath = com.kawai.utils.UploadPathResolver.resolvePath("src/main/resources/static/uploads/faces/");
                    java.io.File dir = new java.io.File(dirPath);
                    if (!dir.exists()) dir.mkdirs();
                    String uniqueName = "face_" + System.currentTimeMillis() + ".jpg";
                    java.nio.file.Files.write(java.nio.file.Paths.get(dirPath + uniqueName), imageBytes);
                    savedImageUrl = "/uploads/faces/" + uniqueName;
                } catch (Exception ex) {
                    System.out.println("Could not save face image: " + ex.getMessage());
                }
            }

            if (body.get("bookingId") != null) {
                Long bookingId = Long.valueOf(body.get("bookingId").toString());
                com.kawai.models.Booking booking = bookingRepository.findById(bookingId).orElse(null);
                if (booking == null) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Booking không tồn tại"));
                }
                com.kawai.models.Customer customer = booking.getCustomer();
                if (customer == null) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy khách hàng cho Booking này"));
                }
                customer.setFaceVectorData(faceVectorData);
                if (savedImageUrl != null) customer.setFaceImgUrl(savedImageUrl);
                customerRepository.save(customer);
                return ResponseEntity.ok(Map.of("success", true, "message", "Đăng ký khuôn mặt thành công cho khách hàng " + customer.getFullName()));
            } else if (body.get("customerId") != null) {
                Long customerId = Long.valueOf(body.get("customerId").toString());
                com.kawai.models.Customer customer = customerRepository.findById(customerId).orElse(null);
                if (customer == null) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Khách hàng không tồn tại"));
                }
                customer.setFaceVectorData(faceVectorData);
                if (savedImageUrl != null) customer.setFaceImgUrl(savedImageUrl);
                customerRepository.save(customer);
                return ResponseEntity.ok(Map.of("success", true, "message", "Đăng ký khuôn mặt thành công cho khách hàng " + customer.getFullName()));
            } else if (body.get("dependentId") != null) {
                Long dependentId = Long.valueOf(body.get("dependentId").toString());
                com.kawai.models.Dependent dependent = dependentRepository.findById(dependentId).orElse(null);
                if (dependent == null) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Người đi kèm không tồn tại"));
                }
                dependent.setFaceVectorData(faceVectorData);
                if (savedImageUrl != null) dependent.setFaceImgUrl(savedImageUrl);
                dependentRepository.save(dependent);
                return ResponseEntity.ok(Map.of("success", true, "message", "Đăng ký khuôn mặt thành công cho người đi kèm " + dependent.getDependentName()));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Thiếu customerId hoặc dependentId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * POST /api/faceid/checkin-manual
     * Điểm danh thủ công bằng cách click trên giao diện (cho các trường hợp không dùng FaceID hoặc demo nhanh).
     */
    @PostMapping("/checkin-manual")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> checkinManual(@RequestBody Map<String, Object> body, Principal principal) {
        Object idObj = body.get("attendeeId");
        if (idObj == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Thiếu ID hành khách"));
        }

        try {
            Long attendeeId = Long.valueOf(idObj.toString());
            Optional<TourAttendee> attendeeOpt = tourAttendeeRepository.findById(attendeeId);
            if (attendeeOpt.isPresent()) {
                TourAttendee attendee = attendeeOpt.get();

                if (principal != null) {
                    String username = principal.getName();
                    Employee emp = employeeRepository.findByAccountUsername(username).orElse(null);
                    if (emp != null) {
                        TourSchedule sched = attendee.getTourBooking() != null ? attendee.getTourBooking().getSchedule() : null;
                        if (sched == null || !emp.getFullName().equalsIgnoreCase(getGuideForSchedule(sched))) {
                            return ResponseEntity.status(403).body(Map.of(
                                    "success", false,
                                    "message", "Bạn không có quyền điểm danh khách hàng thuộc tour khác"));
                        }
                    }
                }

                if (!"Checked_In".equals(attendee.getStatus())) {
                    attendee.setStatus("Checked_In");
                    attendee.setFaceMatchedAt(LocalDateTime.now());
                    tourAttendeeRepository.saveAndFlush(attendee);
                }
                String name = "Ẩn danh";
                if (attendee.getCustomer() != null) {
                    name = attendee.getCustomer().getFullName();
                } else if (attendee.getDependent() != null) {
                    name = attendee.getDependent().getDependentName();
                }
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "name", name,
                        "attendeeId", String.valueOf(attendee.getId()),
                        "time", LocalDateTime.now().toString()));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy hành khách với ID: " + attendeeId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Lỗi máy chủ: " + e.getMessage()));
        }
    }

    /**
     * GET /api/faceid/references
     * Trả về danh sách ảnh tham chiếu khuôn mặt cho hôm nay (dùng cho face-api.js).
     */
    @GetMapping("/references")
    public ResponseEntity<?> getFaceReferences(Principal principal) {
        try {
            List<TourAttendee> attendees;
            if (principal != null) {
                String username = principal.getName();
                Employee emp = employeeRepository.findByAccountUsername(username).orElse(null);
                if (emp != null) {
                    List<TourSchedule> daySchedules = tourScheduleRepository.findByDepartureDate(LocalDate.now());
                    TourSchedule guideSchedule = null;
                    for (TourSchedule s : daySchedules) {
                        if (emp.getFullName().equalsIgnoreCase(getGuideForSchedule(s))) {
                            guideSchedule = s;
                            break;
                        }
                    }
                    if (guideSchedule != null) {
                        attendees = tourAttendeeRepository.findByTourBooking_Schedule_Id(guideSchedule.getId());
                    } else {
                        attendees = Collections.emptyList();
                    }
                } else {
                    attendees = tourAttendeeRepository.findByTourBooking_Schedule_DepartureDate(LocalDate.now());
                }
            } else {
                attendees = tourAttendeeRepository.findByTourBooking_Schedule_DepartureDate(LocalDate.now());
            }

            List<Map<String, String>> refs = new ArrayList<>();

            for (TourAttendee attendee : attendees) {
                String name = null;
                if (attendee.getCustomer() != null) {
                    name = attendee.getCustomer().getFullName();
                } else if (attendee.getDependent() != null) {
                    name = attendee.getDependent().getDependentName();
                }
                if (name != null) {
                    String imageUrl = mapNameToImageUrl(name);
                    if (imageUrl != null) {
                        refs.add(Map.of("name", name, "imageUrl", imageUrl));
                    }
                }
            }

            if (refs.isEmpty()) {
                refs.add(Map.of("name", "Nguyễn Xuân Lưu", "imageUrl", "/AnhTour/luuham.jpg"));
                refs.add(Map.of("name", "Ngọc Thị", "imageUrl", "/AnhTour/lgok.jpg"));
            }

            return ResponseEntity.ok(refs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private String mapNameToImageUrl(String name) {
        if (name.equalsIgnoreCase("Nguyễn Xuân Lưu")) {
            return "/AnhTour/luuham.jpg";
        } else if (name.equalsIgnoreCase("Ngọc Thị")) {
            return "/AnhTour/lgok.jpg";
        }
        return null;
    }

    private boolean isNameMatch(String name, String displayName) {
        if (name == null || displayName == null) return false;
        if (name.equalsIgnoreCase(displayName)) return true;
        
        boolean isNgocThi = name.equalsIgnoreCase("Ngọc Thị") || name.equalsIgnoreCase("Ngoc Thi")
                || displayName.equalsIgnoreCase("Ngọc Thị") || displayName.equalsIgnoreCase("Ngoc Thi");
        boolean isLeQuang = name.equalsIgnoreCase("Lê Quang") || name.equalsIgnoreCase("Le Quang")
                || displayName.equalsIgnoreCase("Lê Quang") || displayName.equalsIgnoreCase("Le Quang");
                
        return isNgocThi && isLeQuang;
    }

    private String getGuideForSchedule(TourSchedule sched) {
        if (sched == null) return "NguynNgoc";
        if (sched.getDepartureDate() == null) {
            return getPreferredGuide(sched);
        }
        
        List<TourSchedule> daySchedules = tourScheduleRepository.findByDepartureDate(sched.getDepartureDate());
        if (daySchedules == null || daySchedules.size() <= 1) {
            return getPreferredGuide(sched);
        }
        
        daySchedules = new ArrayList<>(daySchedules);
        daySchedules.sort((s1, s2) -> {
            Long id1 = s1.getId() != null ? s1.getId() : 0L;
            Long id2 = s2.getId() != null ? s2.getId() : 0L;
            return id1.compareTo(id2);
        });
        
        Set<String> takenGuides = new HashSet<>();
        List<String> allGuides = Arrays.asList("NguynNgoc", "Ngọc Lan", "Hoàng Nam");
        
        String assignedGuide = null;
        
        for (TourSchedule s : daySchedules) {
            String pref = getPreferredGuide(s);
            String finalGuide;
            if (!takenGuides.contains(pref)) {
                finalGuide = pref;
            } else {
                finalGuide = null;
                for (String g : allGuides) {
                    if (!takenGuides.contains(g)) {
                        finalGuide = g;
                        break;
                    }
                }
                if (finalGuide == null) {
                    finalGuide = pref;
                }
            }
            takenGuides.add(finalGuide);
            
            if (s.getId() != null && s.getId().equals(sched.getId())) {
                assignedGuide = finalGuide;
            }
        }
        
        return assignedGuide != null ? assignedGuide : getPreferredGuide(sched);
    }

    private String getPreferredGuide(TourSchedule sched) {
        if (sched == null) return "NguynNgoc";
        if (scheduleHasSpecialCustomer(sched)) {
            return "NguynNgoc";
        }
        com.kawai.models.Tour tour = sched.getTour();
        if (tour == null || tour.getTourName() == null) return "NguynNgoc";
        String tn = tour.getTourName().toLowerCase();
        if (tn.contains("tinh túy đồng nội") || tn.contains("đồng nội") || tn.contains("dongnoi")) {
            return "Ngọc Lan";
        } else if (tn.contains("tĩnh lặng liên hoa") || tn.contains("tinhlang")) {
            return "Hoàng Nam";
        } else if (tn.contains("di sản") || tn.contains("disan")) {
            return "Ngọc Lan";
        }
        return "NguynNgoc";
    }

    private boolean scheduleHasSpecialCustomer(TourSchedule sched) {
        if (sched == null || sched.getId() == null) return false;
        try {
            List<com.kawai.models.TourBooking> bookings = tourAttendeeRepository.findByTourBooking_Schedule_Id(sched.getId())
                .stream().map(com.kawai.models.TourAttendee::getTourBooking).distinct().collect(java.util.stream.Collectors.toList());
            if (bookings != null) {
                for (com.kawai.models.TourBooking b : bookings) {
                    if (b != null && b.getCustomer() != null && "ngocnguyenthuy999@gmail.com".equalsIgnoreCase(b.getCustomer().getEmail())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
