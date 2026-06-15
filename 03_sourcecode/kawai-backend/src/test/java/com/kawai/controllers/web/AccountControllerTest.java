package com.kawai.controllers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawai.dto.CreateEmployeeDTO;
import com.kawai.models.Role;
import com.kawai.repositories.RoleRepository;
import com.kawai.services.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateCustomerAccountWithDefaultUsername() throws Exception {
        // Arrange
        Map<String, String> payload = new HashMap<>();
        payload.put("type", "Khách hàng");
        payload.put("email", "guest@example.com");
        payload.put("name", "Guest Name");
        payload.put("role", "ROLE_GUEST");
        // Không truyền username và password (để test tự tạo default)

        Role mockRole = new Role();
        mockRole.setId(3L);
        when(roleRepository.findByRoleName("ROLE_GUEST")).thenReturn(Optional.of(mockRole));

        // Act & Assert
        mockMvc.perform(post("/admin/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Created successfully"));

        // Verify user service was called
        verify(userService, times(1)).createCustomerAccount(any(CreateEmployeeDTO.class));
    }

    @Test
    public void testDeleteAccountDataIntegrityViolation() throws Exception {
        // Arrange
        doThrow(new org.springframework.dao.DataIntegrityViolationException("Constraint violation"))
                .when(userService).deleteEmployeeAccount(1L);

        // Act & Assert
        mockMvc.perform(delete("/admin/api/v1/accounts/E-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tài khoản đang có dữ liệu ràng buộc (logs, bookings...), không thể xóa cứng."));
    }

    @Test
    public void testUpdateAccount() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "New Employee Name");
        payload.put("email", "new_email@example.com");

        mockMvc.perform(put("/admin/api/v1/accounts/E-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Updated successfully"));

        verify(userService, times(1)).updateEmployeeAccount(eq(1L), any(Map.class));
    }
}
