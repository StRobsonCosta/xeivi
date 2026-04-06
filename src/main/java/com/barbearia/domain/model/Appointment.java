package com.barbearia.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "appointments")
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

    protected Appointment() {
    }

    public Appointment(Customer customer, ServiceOffer serviceOffer, LocalDateTime scheduledAt, double ownerSharePercentage) {
        this.customer = Objects.requireNonNull(customer, "customer is required");
        this.serviceOffer = Objects.requireNonNull(serviceOffer, "serviceOffer is required");
        this.scheduledAt = Objects.requireNonNull(scheduledAt, "scheduledAt is required");
        this.ownerSharePercentage = ownerSharePercentage;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ServiceOffer getServiceOffer() {
        return serviceOffer;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public boolean isPaid() {
        return paid;
    }

    public void markPaid() {
        this.paid = true;
    }

    public double getOwnerSharePercentage() {
        return ownerSharePercentage;
    }

    public double getBarberRevenue() {
        return serviceOffer.getPrice() * (1.0 - ownerSharePercentage / 100.0);
    }

    public double getOwnerRevenue() {
        return serviceOffer.getPrice() * (ownerSharePercentage / 100.0);
    }
}
