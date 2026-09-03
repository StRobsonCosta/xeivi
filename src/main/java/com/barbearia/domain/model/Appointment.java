package com.barbearia.domain.model;

import com.barbearia.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Appointment {

  public enum Status {
    SCHEDULED,
    CONFIRMED,
    CANCELLED,
    COMPLETED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Customer customer;

  @ManyToOne(optional = false)
  private ServiceOffer serviceOffer;

  @ManyToOne private User barber;

  private LocalDateTime scheduledAt;
  private boolean paid;
  private double ownerSharePercentage;

  @Enumerated(EnumType.STRING)
  private Status status = Status.SCHEDULED;

  public Appointment(
      Customer customer,
      ServiceOffer serviceOffer,
      User barber,
      LocalDateTime scheduledAt,
      double ownerSharePercentage) {
    this.customer = Objects.requireNonNull(customer, "customer is required");
    this.serviceOffer = Objects.requireNonNull(serviceOffer, "serviceOffer is required");
    this.barber = barber;
    this.scheduledAt = Objects.requireNonNull(scheduledAt, "scheduledAt is required");
    this.ownerSharePercentage = ownerSharePercentage;
    this.status = Status.SCHEDULED;
  }

  public Appointment(
      Customer customer,
      ServiceOffer serviceOffer,
      LocalDateTime scheduledAt,
      double ownerSharePercentage) {
    this(customer, serviceOffer, null, scheduledAt, ownerSharePercentage);
  }

  public void markPaid() {
    this.paid = true;
  }

  public void confirm() {
    this.status = Status.CONFIRMED;
  }

  public void cancel() {
    this.status = Status.CANCELLED;
  }

  public double getBarberRevenue() {
    return serviceOffer.getPrice() * (1.0 - ownerSharePercentage / 100.0);
  }

  public double getOwnerRevenue() {
    return serviceOffer.getPrice() * (ownerSharePercentage / 100.0);
  }
}
