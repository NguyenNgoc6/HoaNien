package com.hoanien.services.interfaces;

import com.hoanien.dto.CreateEmployeeDTO;
import com.hoanien.models.Employee;
import com.hoanien.models.Customer;

public interface UserService {
    Employee createEmployeeAccount(CreateEmployeeDTO dto);
    Customer createCustomerAccount(CreateEmployeeDTO dto);
    
    Employee updateEmployeeAccount(Long id, java.util.Map<String, String> payload);
    Customer updateCustomerAccount(Long id, java.util.Map<String, String> payload);
}
