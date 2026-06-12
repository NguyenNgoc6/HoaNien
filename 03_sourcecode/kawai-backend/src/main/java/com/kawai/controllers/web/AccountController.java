package com.kawai.controllers.web;

import com.kawai.dto.CreateEmployeeDTO;
import com.kawai.repositories.RoleRepository;
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

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> payload) {
        try {
            CreateEmployeeDTO dto = new CreateEmployeeDTO();
            dto.setFullName(payload.get("name"));
            dto.setEmail(payload.get("email"));
            dto.setPhone(payload.get("phone"));
            dto.setCccd(payload.get("cccd"));
            dto.setGender(payload.get("gender"));
            dto.setPassword(payload.get("password"));
            dto.setUsername(payload.get("username"));
            if (payload.containsKey("salary") && payload.get("salary") != null && !payload.get("salary").isEmpty()) {
                dto.setSalary(new java.math.BigDecimal(payload.get("salary")));
            } else {
                dto.setSalary(java.math.BigDecimal.ZERO);
            }
            if (payload.containsKey("role") && payload.get("role") != null) {
                com.kawai.models.Role role = roleRepository.findByRoleName(payload.get("role"))
                        .orElseThrow(() -> new IllegalArgumentException("Role not found"));
                dto.setRoleId(role.getId());
            }

            if ("Nhân viên".equals(payload.get("type"))) {
                userService.createEmployeeAccount(dto);
            } else {
                // Generate default values for customer if missing from form
                if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
                    dto.setUsername(dto.getEmail() != null ? dto.getEmail().split("@")[0]
                            : "customer" + System.currentTimeMillis());
                }
                if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                    dto.setPassword("123");
                }
                userService.createCustomerAccount(dto);
            }
            return ResponseEntity.ok(Map.of("message", "Created successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Create failed"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable String id, @RequestBody Map<String, String> payload) {
        try {
            Long numericId = Long.parseLong(id.substring(2));
            if (id.startsWith("E-")) {
                userService.updateEmployeeAccount(numericId, payload);
            } else if (id.startsWith("C-")) {
                userService.updateCustomerAccount(numericId, payload);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
            return ResponseEntity.ok(Map.of("message", "Updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable String id) {
        try {
            Long numericId = Long.parseLong(id.substring(2));
            if (id.startsWith("E-")) {
                userService.deleteEmployeeAccount(numericId);
            } else if (id.startsWith("C-")) {
                userService.deleteCustomerAccount(numericId);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Tài khoản đang có dữ liệu ràng buộc (logs, bookings...), không thể xóa cứng."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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
                "status", active));
    }
}
