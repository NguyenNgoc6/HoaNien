package com.hoanien.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class OrderFoodController {

    @Autowired
    private com.hoanien.repositories.FoodItemRepository foodItemRepository;
    @Autowired
    private com.hoanien.repositories.RestaurantTableRepository restaurantTableRepository;
    @Autowired
    private com.hoanien.repositories.RoomRepository roomRepository;

    @GetMapping("/order-food")
    public String showOrderFoodPage(Principal principal, Model model) {
        model.addAttribute("isLoggedIn", principal != null);
        model.addAttribute("menuItems", foodItemRepository.findAll());
        model.addAttribute("tables", restaurantTableRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
        return "guest/order-food";
    }
}
