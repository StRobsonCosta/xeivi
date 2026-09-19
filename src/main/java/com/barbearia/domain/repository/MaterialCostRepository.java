package com.barbearia.domain.repository;

import com.barbearia.domain.model.MaterialCost;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialCostRepository extends JpaRepository<MaterialCost, Long> {
  List<MaterialCost> findByCostDateBetween(LocalDate start, LocalDate end);
}
