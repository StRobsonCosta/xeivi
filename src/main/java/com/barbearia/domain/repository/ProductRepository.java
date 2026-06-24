package com.barbearia.domain.repository;

import com.barbearia.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByName(String name);
	boolean existsByName(String name);
}
