package com.barbearia.domain.repository;

import com.barbearia.domain.model.MaterialCost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MaterialCostRepository extends JpaRepository<MaterialCost, Long> {
    List<MaterialCost> findByCostDateBetween(LocalDate start, LocalDate end);
}
