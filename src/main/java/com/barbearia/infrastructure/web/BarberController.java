package com.barbearia.infrastructure.web;

import com.barbearia.application.dto.AppointmentResponse;
import com.barbearia.application.service.BarberReportService;
import com.barbearia.domain.model.Appointment;
import com.barbearia.domain.repository.AppointmentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/barbers")
public class BarberController {

  private final BarberReportService barberReportService;
  private final AppointmentRepository appointmentRepository;

  public BarberController(
      BarberReportService barberReportService, AppointmentRepository appointmentRepository) {
    this.barberReportService = barberReportService;
    this.appointmentRepository = appointmentRepository;
  }

  @GetMapping("/schedule")
  public ResponseEntity<List<AppointmentResponse>> schedule(
      @RequestParam(name = "date", required = false) LocalDate date) {
    LocalDate queryDate = date == null ? LocalDate.now() : date;
    return ResponseEntity.ok(barberReportService.getScheduleForDay(queryDate));
  }

  @GetMapping("/earnings")
  public ResponseEntity<Map<String, Object>> earnings(
      @RequestParam(name = "date", required = false) LocalDate date,
      @RequestParam(name = "until", required = false) LocalDate until) {
    LocalDate queryDate = date == null ? LocalDate.now() : date;
    LocalDate endDate = until == null ? queryDate : until;
    double dailyEarnings = barberReportService.getDailyEarnings(queryDate);
    Map<LocalDate, Double> projection = barberReportService.getProjection(queryDate, endDate);
    return ResponseEntity.ok(
        Map.of(
            "dailyEarnings", dailyEarnings,
            "projection", projection));
  }

  @PostMapping("/appointments/{id}/confirm")
  @Transactional
  public ResponseEntity<AppointmentResponse> confirm(@PathVariable Long id) {
    Appointment a =
        appointmentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    a.confirm();
    Appointment saved = appointmentRepository.save(a);
    return ResponseEntity.ok(
        barberReportService.getScheduleForDay(saved.getScheduledAt().toLocalDate()).stream()
            .filter(r -> r.getAppointmentId().equals(saved.getId()))
            .findFirst()
            .orElse(null));
  }

  @PostMapping("/appointments/{id}/cancel")
  @Transactional
  public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
    Appointment a =
        appointmentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    a.cancel();
    Appointment saved = appointmentRepository.save(a);
    return ResponseEntity.ok(
        barberReportService.getScheduleForDay(saved.getScheduledAt().toLocalDate()).stream()
            .filter(r -> r.getAppointmentId().equals(saved.getId()))
            .findFirst()
            .orElse(null));
  }
}
