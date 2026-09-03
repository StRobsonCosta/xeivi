package com.barbearia.application.service;

import com.barbearia.application.dto.AppointmentRequest;
import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.application.dto.ProductDto;
import com.barbearia.application.dto.ServiceOfferDto;
import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.model.Customer;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.AppointmentRepository;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.domain.repository.ProductRepository;
import com.barbearia.domain.repository.ServiceOfferRepository;
import com.barbearia.model.User;
import com.barbearia.repo.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientAppointmentService {

  private static final Logger logger = LoggerFactory.getLogger(ClientAppointmentService.class);

  private final CustomerRepository customerRepository;
  private final ServiceOfferRepository serviceOfferRepository;
  private final ProductRepository productRepository;
  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;
  private final MeterRegistry meterRegistry;

  public ClientAppointmentService(
      CustomerRepository customerRepository,
      ServiceOfferRepository serviceOfferRepository,
      ProductRepository productRepository,
      AppointmentRepository appointmentRepository,
      UserRepository userRepository,
      MeterRegistry meterRegistry) {
    this.customerRepository = customerRepository;
    this.serviceOfferRepository = serviceOfferRepository;
    this.productRepository = productRepository;
    this.appointmentRepository = appointmentRepository;
    this.userRepository = userRepository;
    this.meterRegistry = meterRegistry;
  }

  public List<ServiceOfferDto> listServices() {
    return serviceOfferRepository.findAll().stream()
        .map(
            service ->
                new ServiceOfferDto(
                    service.getId(),
                    service.getName(),
                    service.getDescription(),
                    service.getPrice()))
        .collect(Collectors.toList());
  }

  public List<ProductDto> listProducts() {
    return productRepository.findAll().stream()
        .map(
            product ->
                new ProductDto(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice()))
        .collect(Collectors.toList());
  }

  @Transactional
  public AppointmentResponse schedule(AppointmentRequest request) {
    Customer customer =
        customerRepository
            .findById(request.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    ServiceOffer serviceOffer =
        serviceOfferRepository
            .findById(request.getServiceOfferId())
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

    User barber = null;
    if (request.getBarberId() != null) {
      barber =
          userRepository
              .findById(request.getBarberId())
              .orElseThrow(() -> new IllegalArgumentException("Barbeiro não encontrado"));
      if (!"BARBEIRO".equalsIgnoreCase(barber.getRole())) {
        throw new IllegalArgumentException("Usuário selecionado não é barbeiro");
      }

      // scheduledAt must be aligned to 30-minute slots (e.g., 08:00, 08:30)
      var scheduledAt = request.getScheduledAt();
      if (scheduledAt.getMinute() % 30 != 0
          || scheduledAt.getSecond() != 0
          || scheduledAt.getNano() != 0) {
        throw new IllegalArgumentException(
            "Horário deve estar alinhado a intervalos de 30 minutos (ex: 13:00, 13:30)");
      }

      // check availability by verifying there is no overlapping appointment for the barber
      // an overlap exists when: existingStart < requestedEnd && requestedStart < existingEnd
      var windowStart = scheduledAt.minusMinutes(29);
      var windowEnd = scheduledAt.plusMinutes(29);
      var nearby =
          appointmentRepository.findByBarberIdAndScheduledAtBetween(
              barber.getId(), windowStart, windowEnd);
      if (!nearby.isEmpty()) {
        throw new IllegalArgumentException("Horário não disponível para o barbeiro selecionado");
      }
    }

    Appointment appointment =
        new Appointment(
            customer,
            serviceOffer,
            barber,
            request.getScheduledAt(),
            request.getOwnerSharePercentage());
    appointment.markPaid();
    Appointment saved = appointmentRepository.save(appointment);

    meterRegistry.counter("barbearia.appointments.scheduled").increment();
    meterRegistry
        .counter("barbearia.revenue.total", "source", "appointment")
        .increment(serviceOffer.getPrice());

    logger.info(
        "Agendamento criado: cliente={}, serviço={}, horário={}",
        customer.getName(),
        serviceOffer.getName(),
        request.getScheduledAt());

    AppointmentResponse response =
        new AppointmentResponse(
            saved.getId(),
            customer.getId(),
            serviceOffer.getName(),
            saved.getScheduledAt(),
            serviceOffer.getPrice(),
            saved.getOwnerRevenue(),
            saved.getBarberRevenue(),
            saved.getStatus().name());
    if (saved.getBarber() != null) {
      response.setBarberId(saved.getBarber().getId());
      response.setBarberName(saved.getBarber().getUsername());
    }

    return response;
  }

  public java.util.List<java.time.LocalDateTime> getAvailability(
      Long barberId, java.time.LocalDate date) {
    java.time.LocalDateTime start = date.atTime(8, 0);
    java.time.LocalDateTime end = date.atTime(19, 0);
    var appointments =
        appointmentRepository.findByBarberIdAndScheduledAtBetweenOrderByScheduledAtAsc(
            barberId, start, end);
    java.util.List<java.time.LocalDateTime> slots = new java.util.ArrayList<>();
    java.time.LocalDateTime cur = start;
    while (!cur.isAfter(end.minusMinutes(30))) {
      boolean overlaps = false;
      var slotStart = cur;
      var slotEnd = cur.plusMinutes(30);
      for (var a : appointments) {
        var aStart = a.getScheduledAt();
        var aEnd = aStart.plusMinutes(30);
        if (aStart.isBefore(slotEnd) && slotStart.isBefore(aEnd)) {
          overlaps = true;
          break;
        }
      }
      if (!overlaps) slots.add(cur);
      cur = cur.plusMinutes(30);
    }
    return slots;
  }
}
