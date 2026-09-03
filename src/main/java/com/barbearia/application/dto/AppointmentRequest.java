package com.barbearia.application.dto;

import java.time.LocalDateTime;

public class AppointmentRequest {

  private Long customerId;
  private Long serviceOfferId;
  private Long barberId;
  private LocalDateTime scheduledAt;
  private double ownerSharePercentage;

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getServiceOfferId() {
    return serviceOfferId;
  }

  public void setServiceOfferId(Long serviceOfferId) {
    this.serviceOfferId = serviceOfferId;
  }

  public Long getBarberId() {
    return barberId;
  }

  public void setBarberId(Long barberId) {
    this.barberId = barberId;
  }

  public LocalDateTime getScheduledAt() {
    return scheduledAt;
  }

  public void setScheduledAt(LocalDateTime scheduledAt) {
    this.scheduledAt = scheduledAt;
  }

  public double getOwnerSharePercentage() {
    return ownerSharePercentage;
  }

  public void setOwnerSharePercentage(double ownerSharePercentage) {
    this.ownerSharePercentage = ownerSharePercentage;
  }
}
