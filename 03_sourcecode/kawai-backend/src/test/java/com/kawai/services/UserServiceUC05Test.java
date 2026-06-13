package com.kawai.services;

import com.kawai.dto.CreateEmployeeDTO;
import com.kawai.models.Account;
import com.kawai.models.Employee;
import com.kawai.models.Role;
import com.kawai.repositories.AccountRepository;
import com.kawai.repositories.EmployeeRepository;
import com.kawai.repositories.RoleRepository;
import com.kawai.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kawai.repositories.CustomerRepository;
import com.kawai.models.Customer;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUC05Test {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateEmployeeAccount_Success() {
        // Arrange
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        dto.setUsername("staff_new");
        dto.setPassword("password123");
        dto.setRoleId(2L); // 2L is for example ROLE_FB_STAFF
        dto.setFullName("Nguyen Van Staff");
        dto.setGender("MALE");
        dto.setCccd("012345678912");
        dto.setPhone("0987654321");
        dto.setEmail("staff@example.com");
        dto.setSalary(new BigDecimal("10000000"));

        Role mockRole = new Role();
        mockRole.setId(2L);
        mockRole.setRoleName("ROLE_FB_STAFF");

        when(roleRepository.findById(2L)).thenReturn(Optional.of(mockRole));
        when(accountRepository.existsByUsername("staff_new")).thenReturn(false);
        when(employeeRepository.existsByCccd("012345678912")).thenReturn(false);
        when(employeeRepository.existsByEmail("staff@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password123");

        Account savedAccount = new Account();
        savedAccount.setId(1L);
        savedAccount.setUsername("staff_new");
        savedAccount.setPasswordHash("encoded_password123");
        savedAccount.setRole(mockRole);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Employee savedEmployee = new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setAccount(savedAccount);
        savedEmployee.setFullName("Nguyen Van Staff");
        savedEmployee.setGender("MALE");
        savedEmployee.setCccd("012345678912");
        savedEmployee.setPhone("0987654321");
        savedEmployee.setEmail("staff@example.com");
        savedEmployee.setSalary(new BigDecimal("10000000"));

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act
        Employee result = userService.createEmployeeAccount(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Nguyen Van Staff", result.getFullName());
        assertNotNull(result.getAccount());
        assertEquals("staff_new", result.getAccount().getUsername());
        assertEquals("ROLE_FB_STAFF", result.getAccount().getRole().getRoleName());

        verify(accountRepository, times(1)).save(any(Account.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testCreateCustomerAccount_Success() {
        // Arrange
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        dto.setUsername("customer123");
        dto.setPassword("pass");
        dto.setRoleId(3L);
        dto.setFullName("Tran Customer");
        dto.setEmail("cust@example.com");

        Role mockRole = new Role();
        mockRole.setId(3L);
        mockRole.setRoleName("ROLE_GUEST");

        when(roleRepository.findById(3L)).thenReturn(Optional.of(mockRole));
        when(accountRepository.existsByUsername("customer123")).thenReturn(false);
        when(customerRepository.existsByEmail("cust@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded_pass");

        Account savedAccount = new Account();
        savedAccount.setId(2L);
        savedAccount.setUsername("customer123");
        savedAccount.setRole(mockRole);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setAccount(savedAccount);
        savedCustomer.setFullName("Tran Customer");
        savedCustomer.setEmail("cust@example.com");

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // Act
        Customer result = userService.createCustomerAccount(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Tran Customer", result.getFullName());
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateEmployeeAccount_ToggleStatus() {
        // Arrange
        Long employeeId = 1L;
        Employee mockEmployee = new Employee();
        mockEmployee.setId(employeeId);

        Account mockAccount = new Account();
        mockAccount.setIsActive(true); // Trạng thái ban đầu đang bật
        mockEmployee.setAccount(mockAccount);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        // Act - Gửi payload với yêu cầu vô hiệu hóa tài khoản (status = false)
        java.util.Map<String, String> payload = new java.util.HashMap<>();
        payload.put("status", "false");
        Employee result = userService.updateEmployeeAccount(employeeId, payload);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAccount());
        assertFalse(result.getAccount().getIsActive(), "Account status should be updated to false");

        // Verify that the repositories were called to save the new state
        verify(accountRepository, times(1)).save(mockAccount);
        verify(employeeRepository, times(1)).save(mockEmployee);
    }

    @Test
    void testDeleteEmployeeAccount_Success() {
        // Arrange
        Long employeeId = 1L;
        Employee mockEmployee = new Employee();
        mockEmployee.setId(employeeId);
        Account mockAccount = new Account();
        mockAccount.setId(1L);
        mockEmployee.setAccount(mockAccount);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(mockEmployee));

        // Act
        userService.deleteEmployeeAccount(employeeId);

        // Assert
        verify(employeeRepository, times(1)).delete(mockEmployee);
        verify(accountRepository, times(1)).delete(mockAccount);
    }
}
