package com.barbearia.infrastructure;

import com.barbearia.domain.model.Customer;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.AppointmentRepository;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.domain.repository.MaterialCostRepository;
import com.barbearia.domain.repository.ProductRepository;
import com.barbearia.domain.repository.ServiceOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartupDataLoaderTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServiceOfferRepository serviceOfferRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MaterialCostRepository materialCostRepository;

    @InjectMocks
    private StartupDataLoader loader;

    @BeforeEach
    void setup() {
        // Default stubs: first run -> not present, second run -> present
        when(customerRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Customer("Alice Silva", "alice@example.com", "+55 11 99999-0001")));

        when(customerRepository.findByEmail("bruno@example.com"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Customer("Bruno Costa", "bruno@example.com", "+55 11 99999-0002")));

        when(serviceOfferRepository.findByName("Corte Clássico"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new ServiceOffer("Corte Clássico", "Corte masculino completo", 45.0)));

        when(serviceOfferRepository.findByName("Barba Premium"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new ServiceOffer("Barba Premium", "Acabamento e tratamento da barba", 35.0)));

        when(productRepository.findByName("Pomada Modeladora"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new com.barbearia.domain.model.Product("Pomada Modeladora", "Fixação média", 28.0)));

        when(productRepository.findByName("Óleo de Barba"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new com.barbearia.domain.model.Product("Óleo de Barba", "Hidratação e brilho", 52.0)));

        // appointment/material counts: first run 0, second run >0 to simulate inserts
        when(appointmentRepository.count()).thenReturn(0L).thenReturn(2L);
        when(materialCostRepository.count()).thenReturn(0L).thenReturn(2L);

                // Ensure save returns the entity passed (avoid nulls from mocks)
                when(customerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
                when(serviceOfferRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
                when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
                when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
                when(materialCostRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void loaderIsIdempotent() throws Exception {
        // Run loader twice
        loader.run(null);
        loader.run(null);

                // Verify saves called only on first run: two customers, two service offers, two products, two appointments, two material costs
                verify(customerRepository, times(2)).save(any(Customer.class));
                verify(serviceOfferRepository, times(2)).save(any(ServiceOffer.class));
                verify(productRepository, times(2)).save(any());
                verify(appointmentRepository, times(2)).save(any());
                verify(materialCostRepository, times(2)).save(any());
    }
}
