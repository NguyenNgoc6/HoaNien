package com.kawai.controllers.web;

import com.kawai.config.SecurityConfig;
import com.kawai.services.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
public class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.kawai.repositories.AccountRepository accountRepository;

    @MockBean
    private com.kawai.repositories.EmployeeRepository employeeRepository;

    @MockBean
    private com.kawai.repositories.CustomerRepository customerRepository;

    @MockBean
    private com.kawai.repositories.RoleRepository roleRepository;

    @MockBean
    private com.kawai.repositories.RoomCategoryRepository roomCategoryRepository;

    @MockBean
    private com.kawai.repositories.RoomRepository roomRepository;

    @MockBean
    private com.kawai.repositories.FoodItemRepository foodItemRepository;

    @MockBean
    private com.kawai.repositories.TourRepository tourRepository;

    @MockBean
    private com.kawai.repositories.PromotionRepository promotionRepository;

    @MockBean
    private com.kawai.repositories.DailyRateRepository dailyRateRepository;

    @MockBean
    private com.kawai.repositories.BookingRepository bookingRepository;

    @MockBean
    private com.kawai.repositories.AuditLogRepository auditLogRepository;

    @MockBean
    private com.kawai.repositories.ReviewRepository reviewRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "FB_STAFF")
    public void TC_M1_016_nonAdminAccessAdminApi_Returns403() throws Exception {
        mockMvc.perform(get("/admin/master-data"))
                .andExpect(status().isForbidden());
    }
}
