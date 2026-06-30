package com.barbearia.domain.repository;

import com.barbearia.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query(value = "SELECT * FROM customers WHERE email = ?1 LIMIT 1", nativeQuery = true)
    Optional<Customer> findFirstByEmail(String email);
    
    boolean existsByEmail(String email);
}
