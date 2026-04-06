package com.barbearia.application.service;

import com.barbearia.application.dto.OwnerDashboardDto;
import com.barbearia.application.dto.OwnerDashboardDto.DailyMovement;
import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.repository.AppointmentRepository;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.domain.repository.MaterialCostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OwnerDashboardService {

    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;
    private final MaterialCostRepository materialCostRepository;

    public OwnerDashboardService(CustomerRepository customerRepository,
                                 AppointmentRepository appointmentRepository,
                                 MaterialCostRepository materialCostRepository) {
        this.customerRepository = customerRepository;
        this.appointmentRepository = appointmentRepository;
        this.materialCostRepository = materialCostRepository;
    }

    public OwnerDashboardDto getOwnerDashboard(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        List<Appointment> appointments = appointmentRepository.findByScheduledAtBetween(start, end);
        double grossRevenue = appointments.stream()
                .mapToDouble(appointment -> appointment.getServiceOffer().getPrice())
                .sum();

        double ownerProfit = appointments.stream()
                .mapToDouble(Appointment::getOwnerRevenue)
                .sum();

        double totalCosts = materialCostRepository.findByCostDateBetween(startDate, endDate).stream()
                .mapToDouble(cost -> cost.getAmount())
                .sum();

        List<DailyMovement> movements = appointments.stream()
                .collect(Collectors.groupingBy(a -> a.getScheduledAt().toLocalDate()))
                .entrySet().stream()
                .map(entry -> new DailyMovement(entry.getKey(), entry.getValue().size(),
                        entry.getValue().stream().mapToDouble(a -> a.getServiceOffer().getPrice()).sum()))
                .sorted(Comparator.comparing(DailyMovement::getAppointments).reversed())
                .collect(Collectors.toList());

        List<DailyMovement> busiestDays = movements.stream().limit(5).toList();
        List<DailyMovement> slowestDays = movements.stream()
                .sorted(Comparator.comparing(DailyMovement::getAppointments))
                .limit(5)
                .toList();

        return new OwnerDashboardDto(
                customerRepository.count(),
                grossRevenue,
                ownerProfit - totalCosts,
                totalCosts,
                busiestDays,
                slowestDays);
    }
}
