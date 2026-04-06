package com.barbearia.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "material_costs")
public class MaterialCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private double amount;
    private LocalDate costDate;

    protected MaterialCost() {
    }

    public MaterialCost(String description, double amount, LocalDate costDate) {
        this.description = Objects.requireNonNull(description, "description is required");
        this.amount = amount;
        this.costDate = Objects.requireNonNull(costDate, "costDate is required");
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getCostDate() {
        return costDate;
    }
}
