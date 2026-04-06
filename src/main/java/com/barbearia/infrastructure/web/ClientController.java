package com.barbearia.infrastructure.web;

import com.barbearia.application.dto.AppointmentRequest;
import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.application.dto.ProductDto;
import com.barbearia.application.dto.ServiceOfferDto;
import com.barbearia.application.service.ClientAppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientAppointmentService clientAppointmentService;

    public ClientController(ClientAppointmentService clientAppointmentService) {
        this.clientAppointmentService = clientAppointmentService;
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
}
