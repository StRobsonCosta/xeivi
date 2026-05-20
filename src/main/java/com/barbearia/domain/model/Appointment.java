package com.barbearia.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Customer customer;

    @ManyToOne(optional = false)
    private ServiceOffer serviceOffer;

    private LocalDateTime scheduledAt;
    private boolean paid;
    private double ownerSharePercentage;

    public Appointment(Customer customer, ServiceOffer serviceOffer, LocalDateTime scheduledAt, double ownerSharePercentage) {
        this.customer = Objects.requireNonNull(customer, "customer is required");
        this.serviceOffer = Objects.requireNonNull(serviceOffer, "serviceOffer is required");
        this.scheduledAt = Objects.requireNonNull(scheduledAt, "scheduledAt is required");
        this.ownerSharePercentage = ownerSharePercentage;
    }

    public void markPaid() {
        this.paid = true;
    }

    public double getBarberRevenue() {
        return serviceOffer.getPrice() * (1.0 - ownerSharePercentage / 100.0);
    }

    public double getOwnerRevenue() {
        return serviceOffer.getPrice() * (ownerSharePercentage / 100.0);
    }
}
