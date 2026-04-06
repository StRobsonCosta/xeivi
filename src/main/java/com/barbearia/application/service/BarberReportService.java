package com.barbearia.application.service;

import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BarberReportService {

    private final AppointmentRepository appointmentRepository;

    public BarberReportService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<AppointmentResponse> getScheduleForDay(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Appointment> appointments = appointmentRepository.findByScheduledAtBetween(start, end);

        return appointments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public double getDailyEarnings(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return appointmentRepository.findByScheduledAtBetween(start, end)
                .stream()
                .mapToDouble(Appointment::getBarberRevenue)
                .sum();
    }

    public Map<LocalDate, Double> getProjection(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        List<Appointment> appointments = appointmentRepository.findByScheduledAtBetween(start, end);
        return appointments.stream()
                .collect(Collectors.groupingBy(a -> a.getScheduledAt().toLocalDate(),
                        Collectors.summingDouble(Appointment::getBarberRevenue)));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getCustomer().getId(),
                appointment.getServiceOffer().getName(),
                appointment.getScheduledAt(),
                appointment.getServiceOffer().getPrice(),
                appointment.getOwnerRevenue(),
                appointment.getBarberRevenue());
    }
}
