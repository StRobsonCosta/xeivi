package com.barbearia.infrastructure.web;

import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.application.service.BarberReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/barbers")
public class BarberController {

    private final BarberReportService barberReportService;

    public BarberController(BarberReportService barberReportService) {
        this.barberReportService = barberReportService;
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<AppointmentResponse>> schedule(@RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date == null ? LocalDate.now() : date;
        return ResponseEntity.ok(barberReportService.getScheduleForDay(queryDate));
    }

    @GetMapping("/earnings")
    public ResponseEntity<Map<String, Object>> earnings(@RequestParam(required = false) LocalDate date,
                                                        @RequestParam(required = false) LocalDate until) {
        LocalDate queryDate = date == null ? LocalDate.now() : date;
        LocalDate endDate = until == null ? queryDate : until;
        double dailyEarnings = barberReportService.getDailyEarnings(queryDate);
        Map<LocalDate, Double> projection = barberReportService.getProjection(queryDate, endDate);
        return ResponseEntity.ok(Map.of(
                "dailyEarnings", dailyEarnings,
                "projection", projection));
    }
}
