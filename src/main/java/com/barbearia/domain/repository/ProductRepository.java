package com.barbearia.domain.repository;

import com.barbearia.domain.model.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query(value = "SELECT * FROM products WHERE name = ?1 LIMIT 1", nativeQuery = true)
  Optional<Product> findFirstByName(String name);

  boolean existsByName(String name);
}
