package com.barbearia.infrastructure.web;

import com.barbearia.application.dto.OwnerDashboardDto;
import com.barbearia.application.dto.ProductDto;
import com.barbearia.application.dto.ServiceOfferDto;
import com.barbearia.application.service.OwnerDashboardService;
import com.barbearia.domain.model.Product;
import com.barbearia.domain.model.ServiceOffer;
import com.barbearia.domain.repository.ProductRepository;
import com.barbearia.domain.repository.ServiceOfferRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerDashboardService ownerDashboardService;
    private final ProductRepository productRepository;
    private final ServiceOfferRepository serviceOfferRepository;

    public OwnerController(OwnerDashboardService ownerDashboardService,
                           ProductRepository productRepository,
                           ServiceOfferRepository serviceOfferRepository) {
        this.ownerDashboardService = ownerDashboardService;
        this.productRepository = productRepository;
        this.serviceOfferRepository = serviceOfferRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<OwnerDashboardDto> dashboard(@RequestParam(name = "from", required = false) LocalDate from,
                                                       @RequestParam(name = "to", required = false) LocalDate to) {
        LocalDate startDate = from == null ? LocalDate.now().minusDays(7) : from;
        LocalDate endDate = to == null ? LocalDate.now() : to;
        return ResponseEntity.ok(ownerDashboardService.getOwnerDashboard(startDate, endDate));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto) {
        Product p = new Product(dto.getName(), dto.getDescription(), dto.getPrice());
        Product saved = productRepository.save(p);
        return ResponseEntity.ok(new ProductDto(saved.getId(), saved.getName(), saved.getDescription(), saved.getPrice()));
    }

    @PutMapping("/products/{id}")
    @Transactional
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto dto) {
        Product p = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        Product saved = productRepository.save(p);
        return ResponseEntity.ok(new ProductDto(saved.getId(), saved.getName(), saved.getDescription(), saved.getPrice()));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceOfferDto> createService(@RequestBody ServiceOfferDto dto) {
        ServiceOffer s = new ServiceOffer(dto.getName(), dto.getDescription(), dto.getPrice());
        ServiceOffer saved = serviceOfferRepository.save(s);
        return ResponseEntity.ok(new ServiceOfferDto(saved.getId(), saved.getName(), saved.getDescription(), saved.getPrice()));
    }

    @PutMapping("/services/{id}")
    @Transactional
    public ResponseEntity<ServiceOfferDto> updateService(@PathVariable Long id, @RequestBody ServiceOfferDto dto) {
        ServiceOffer s = serviceOfferRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        s.setPrice(dto.getPrice());
        ServiceOffer saved = serviceOfferRepository.save(s);
        return ResponseEntity.ok(new ServiceOfferDto(saved.getId(), saved.getName(), saved.getDescription(), saved.getPrice()));
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceOfferRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
