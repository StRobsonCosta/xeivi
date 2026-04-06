package com.barbearia.application.service;

import com.barbearia.application.dto.AppointmentRequest;
import com.barbearia.domain.model.Customer;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.AppointmentRepository;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.domain.repository.ProductRepository;
import com.barbearia.domain.repository.ServiceOfferRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ClientAppointmentServiceTest {

    private CustomerRepository customerRepository;
    private ServiceOfferRepository serviceOfferRepository;
    private ProductRepository productRepository;
    private AppointmentRepository appointmentRepository;
    private ClientAppointmentService service;

    @BeforeEach
    void setUp() {
        customerRepository = Mockito.mock(CustomerRepository.class);
        serviceOfferRepository = Mockito.mock(ServiceOfferRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        appointmentRepository = Mockito.mock(AppointmentRepository.class);
        service = new ClientAppointmentService(customerRepository, serviceOfferRepository, productRepository,
                appointmentRepository, new SimpleMeterRegistry());
    }

    @Test
    void scheduleCreatesAppointment() {
        Customer customer = new Customer("Test User", "test@example.com", "+55 11 90000-0000");
        ServiceOffer serviceOffer = new ServiceOffer("Corte", "Corte rápido", 30.0);
        setId(customer, 1L);
        setId(serviceOffer, 1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceOfferRepository.findById(1L)).thenReturn(Optional.of(serviceOffer));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> {
            Object arg = invocation.getArgument(0);
            setId(arg, 1L);
            return arg;
        });

        AppointmentRequest request = new AppointmentRequest();
        request.setCustomerId(1L);
        request.setServiceOfferId(1L);
        request.setScheduledAt(LocalDateTime.now().plusDays(1));
        request.setOwnerSharePercentage(30.0);

        var response = service.schedule(request);

        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals("Corte", response.getServiceName());
        assertEquals(30.0, response.getTotalPrice());
        assertEquals(21.0, response.getBarberShare());
    }

    private void setId(Object target, Long id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
