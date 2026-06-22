package com.barbearia.infrastructure.web;

import com.barbearia.application.dto.AppointmentRequest;
import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.application.dto.ProductDto;
import com.barbearia.application.dto.ServiceOfferDto;
import com.barbearia.application.service.ClientAppointmentService;
import com.barbearia.model.User;
import com.barbearia.repo.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientAppointmentService clientAppointmentService;
    private final UserRepository userRepository;

    public ClientController(ClientAppointmentService clientAppointmentService, UserRepository userRepository) {
        this.clientAppointmentService = clientAppointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/services")
    public List<ServiceOfferDto> listServices() {
        return clientAppointmentService.listServices();
    }

    @GetMapping("/products")
    public List<ProductDto> listProducts() {
        return clientAppointmentService.listProducts();
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> scheduleAppointment(@RequestBody AppointmentRequest request) {
        AppointmentResponse response = clientAppointmentService.schedule(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/barbers")
    public List<User> listBarbers() {
        return userRepository.findAll().stream().filter(u -> "BARBEIRO".equalsIgnoreCase(u.getRole())).collect(Collectors.toList());
    }

    @GetMapping("/availability")
    public List<LocalDateTime> availability(@RequestParam Long barberId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // delegate to service
        return clientAppointmentService.getAvailability(barberId, date);
    }
}
