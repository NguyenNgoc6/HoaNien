package com.hoanien.repositories;

import com.hoanien.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAccount_Username(String username);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
}
