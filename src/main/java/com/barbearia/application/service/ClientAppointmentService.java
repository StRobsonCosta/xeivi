package com.barbearia.application.service;

import com.barbearia.application.dto.AppointmentRequest;
import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.application.dto.ProductDto;
import com.barbearia.application.dto.ServiceOfferDto;
import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.model.Customer;
import com.barbearia.domain.model.Product;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.AppointmentRepository;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.domain.repository.ProductRepository;
import com.barbearia.domain.repository.ServiceOfferRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(ClientAppointmentService.class);

    private final CustomerRepository customerRepository;
    private final ServiceOfferRepository serviceOfferRepository;
    private final ProductRepository productRepository;
    private final AppointmentRepository appointmentRepository;
    private final MeterRegistry meterRegistry;

    public ClientAppointmentService(CustomerRepository customerRepository,
                                    ServiceOfferRepository serviceOfferRepository,
                                    ProductRepository productRepository,
                                    AppointmentRepository appointmentRepository,
                                    MeterRegistry meterRegistry) {
        this.customerRepository = customerRepository;
        this.serviceOfferRepository = serviceOfferRepository;
        this.productRepository = productRepository;
        this.appointmentRepository = appointmentRepository;
        this.meterRegistry = meterRegistry;
    }

    public List<ServiceOfferDto> listServices() {
        return serviceOfferRepository.findAll().stream()
                .map(service -> new ServiceOfferDto(service.getId(), service.getName(), service.getDescription(), service.getPrice()))
                .collect(Collectors.toList());
    }

    public List<ProductDto> listProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getPrice()))
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse schedule(AppointmentRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        ServiceOffer serviceOffer = serviceOfferRepository.findById(request.getServiceOfferId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        Appointment appointment = new Appointment(customer, serviceOffer, request.getScheduledAt(), request.getOwnerSharePercentage());
        appointment.markPaid();
        Appointment saved = appointmentRepository.save(appointment);

        meterRegistry.counter("barbearia.appointments.scheduled").increment();
        meterRegistry.counter("barbearia.revenue.total", "source", "appointment").increment(serviceOffer.getPrice());

        logger.info("Agendamento criado: cliente={}, serviço={}, horário={}", customer.getName(), serviceOffer.getName(), request.getScheduledAt());

        return new AppointmentResponse(
                saved.getId(),
                customer.getId(),
                serviceOffer.getName(),
                saved.getScheduledAt(),
                serviceOffer.getPrice(),
                saved.getOwnerRevenue(),
                saved.getBarberRevenue());
    }
}
