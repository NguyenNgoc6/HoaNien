package com.kawai.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class OrderFoodController {

    @Autowired
    private com.kawai.repositories.FoodItemRepository foodItemRepository;
    @Autowired
    private com.kawai.repositories.RestaurantTableRepository restaurantTableRepository;
    @Autowired
    private com.kawai.repositories.RoomRepository roomRepository;
    @Autowired
    private com.kawai.repositories.RoomBookingDetailRepository roomBookingDetailRepository;
    @Autowired
    private com.kawai.repositories.CustomerRepository customerRepository;

    @GetMapping("/order-food")
    public String showOrderFoodPage(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        model.addAttribute("menuItems", foodItemRepository.findAll());
        model.addAttribute("tables", restaurantTableRepository.findAll());
        
        com.kawai.models.Customer currentCustomer = null;
        if (principal != null) {
            currentCustomer = customerRepository.findByAccount_Username(principal.getName()).orElse(null);
        }
        
        java.util.List<java.util.Map<String, String>> roomDataList = new java.util.ArrayList<>();
        for (com.kawai.models.Room r : roomRepository.findAll()) {
            if ("Occupied".equalsIgnoreCase(r.getRoomStatus()) && r.getCurrentBookingDetailId() != null) {
                com.kawai.models.RoomBookingDetail detail = roomBookingDetailRepository.findById(r.getCurrentBookingDetailId()).orElse(null);
                
                // PII FIX: ONLY ADD THE ROOM IF IT BELONGS TO THE LOGGED IN CUSTOMER
                if (detail != null && detail.getCustomer() != null && currentCustomer != null 
                        && detail.getCustomer().getId().equals(currentCustomer.getId())) {
                    
                    java.util.Map<String, String> map = new java.util.HashMap<>();
                    map.put("roomNumber", r.getRoomNumber());
                    map.put("displayName", "Phòng " + r.getRoomNumber() + " (" + r.getCategory().getCategoryName() + ")");
                    map.put("customerInfo", detail.getCustomer().getFullName());
                    map.put("creditLimit", detail.getSubCreditLimit() != null ? detail.getSubCreditLimit().toString() : "0");
                    roomDataList.add(map);
                }
            }
        }
        model.addAttribute("rooms", roomDataList);
        
        return "guest/order-food";
    }

    @Autowired
    private com.kawai.repositories.FoodOrderRepository foodOrderRepository;

    @GetMapping("/order-success")
    public String showOrderSuccessPage(Principal principal, Model model, @org.springframework.web.bind.annotation.RequestParam(required = false) String roomNumber) {
        model.addAttribute("isLoggedIn", principal != null);
        
        com.kawai.models.Customer currentCustomer = null;
        if (principal != null) {
            currentCustomer = customerRepository.findByAccount_Username(principal.getName()).orElse(null);
        }
        
        java.util.List<com.kawai.models.FoodOrder> orders = new java.util.ArrayList<>();
        String errorMessage = null;
        
        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            com.kawai.models.Room room = roomRepository.findByRoomNumber(roomNumber).orElse(null);
            if (room != null && room.getCurrentBookingDetailId() != null) {
                com.kawai.models.RoomBookingDetail detail = roomBookingDetailRepository.findById(room.getCurrentBookingDetailId()).orElse(null);
                
                // IDOR FIX: PREVENT VIEWING ORDERS OF OTHER ROOMS
                if (detail != null && detail.getCustomer() != null && currentCustomer != null 
                        && detail.getCustomer().getId().equals(currentCustomer.getId())) {
                    orders = foodOrderRepository.findByBookingDetailWithDetails(room.getCurrentBookingDetailId());
                } else {
                    errorMessage = "Bạn không có quyền truy cập vào thông tin của phòng này.";
                }
            } else {
                errorMessage = "Phòng không tồn tại hoặc chưa được check-in.";
            }
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("roomNumber", roomNumber);
        model.addAttribute("errorMessage", errorMessage);
        
        return "guest/order-success";
    }
}
