package com.barbearia.application.dto;

import java.time.LocalDateTime;

public class AppointmentResponse {

    private Long appointmentId;
    private Long customerId;
    private String serviceName;
    private LocalDateTime scheduledAt;
    private double totalPrice;
    private double ownerShare;
    private double barberShare;

    public AppointmentResponse(Long appointmentId, Long customerId, String serviceName, LocalDateTime scheduledAt,
                               double totalPrice, double ownerShare, double barberShare) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.serviceName = serviceName;
        this.scheduledAt = scheduledAt;
        this.totalPrice = totalPrice;
        this.ownerShare = ownerShare;
        this.barberShare = barberShare;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getOwnerShare() {
        return ownerShare;
    }

    public double getBarberShare() {
        return barberShare;
    }
}
