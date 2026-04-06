package com.barbearia.application.service;

import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.model.Customer;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BarberReportServiceTest {

    private AppointmentRepository appointmentRepository;
    private BarberReportService service;

    @BeforeEach
    void setUp() {
        appointmentRepository = Mockito.mock(AppointmentRepository.class);
        service = new BarberReportService(appointmentRepository);
    }

    @Test
    void calculatesDailyEarningsAndSchedule() {
        ServiceOffer offer = new ServiceOffer("Barba", "Ajuste fino", 40.0);
        Appointment appointment = new Appointment(new Customer("Cliente", "c@example.com", "+55 11 90000-0000"),
                offer, LocalDateTime.now().plusHours(2), 25.0);
        when(appointmentRepository.findByScheduledAtBetween(any(), any()))
                .thenReturn(List.of(appointment));

        var schedule = service.getScheduleForDay(LocalDate.now());
        assertEquals(1, schedule.size());
        AppointmentResponse response = schedule.get(0);
        assertEquals(40.0, response.getTotalPrice());
        assertEquals(30.0, response.getBarberShare());
        assertEquals(30.0, service.getDailyEarnings(LocalDate.now()));
    }
}
