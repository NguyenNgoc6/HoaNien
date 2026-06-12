package com.kawai.controllers.web;

import com.kawai.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/v1/accounts")
public class AccountController {

    @Autowired
    private UserService userService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable String id,
                                          @RequestBody Map<String, Boolean> body) {

        Boolean active = body.get("status");

        System.out.println("Toggle account " + id + " -> " + active);

        try {
            Long numericId = Long.parseLong(id.substring(2));
            if (id.startsWith("E-")) {
                userService.updateEmployeeAccount(numericId, Map.of("status", String.valueOf(active)));
            } else if (id.startsWith("C-")) {
                userService.updateCustomerAccount(numericId, Map.of("status", String.valueOf(active)));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
        } catch (Exception e) {
            System.err.println("Error toggling account status: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of(
                "id", id,
                "status", active
        ));
    }
}
