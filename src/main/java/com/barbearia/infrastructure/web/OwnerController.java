package com.barbearia.infrastructure.web;

import com.barbearia.application.dto.OwnerDashboardDto;
import com.barbearia.application.service.OwnerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerDashboardService ownerDashboardService;

    public OwnerController(OwnerDashboardService ownerDashboardService) {
        this.ownerDashboardService = ownerDashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<OwnerDashboardDto> dashboard(@RequestParam(required = false) LocalDate from,
                                                       @RequestParam(required = false) LocalDate to) {
        LocalDate startDate = from == null ? LocalDate.now().minusDays(7) : from;
        LocalDate endDate = to == null ? LocalDate.now() : to;
        return ResponseEntity.ok(ownerDashboardService.getOwnerDashboard(startDate, endDate));
    }
}
