package com.barbearia.application.service;

import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.model.Customer;
import com.barbearia.domain.model.MaterialCost;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.AppointmentRepository;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.domain.repository.MaterialCostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OwnerDashboardServiceTest {

    private CustomerRepository customerRepository;
    private AppointmentRepository appointmentRepository;
    private MaterialCostRepository materialCostRepository;
    private OwnerDashboardService service;

    @BeforeEach
    void setUp() {
        customerRepository = Mockito.mock(CustomerRepository.class);
        appointmentRepository = Mockito.mock(AppointmentRepository.class);
        materialCostRepository = Mockito.mock(MaterialCostRepository.class);
        service = new OwnerDashboardService(customerRepository, appointmentRepository, materialCostRepository);
    }

    @Test
    void ownerDashboardSummarizesMetrics() {
        when(customerRepository.count()).thenReturn(2L);
        ServiceOffer offer = new ServiceOffer("Corte", "Corte completo", 50.0);
        Appointment appointment = new Appointment(new Customer("A", "a@example.com", "+55 11 90000-0000"),
                offer, LocalDateTime.now(), 20.0);
        when(appointmentRepository.findByScheduledAtBetween(any(), any()))
                .thenReturn(List.of(appointment));
        when(materialCostRepository.findByCostDateBetween(LocalDate.now(), LocalDate.now()))
                .thenReturn(List.of(new MaterialCost("Produto", 10.0, LocalDate.now())));

        var dashboard = service.getOwnerDashboard(LocalDate.now(), LocalDate.now());

        assertEquals(2L, dashboard.getTotalCustomers());
        assertEquals(50.0, dashboard.getGrossRevenue());
        assertEquals(0.0, dashboard.getOwnerProfit());
        assertEquals(10.0, dashboard.getTotalCosts());
    }
}
