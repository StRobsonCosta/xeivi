package com.barbearia.domain.repository;

import com.barbearia.domain.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  List<Appointment> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);

  List<Appointment> findByScheduledAtBetweenOrderByScheduledAtAsc(
      LocalDateTime start, LocalDateTime end);

  List<Appointment> findByBarberIdAndScheduledAtBetweenOrderByScheduledAtAsc(
      Long barberId, LocalDateTime start, LocalDateTime end);

  List<Appointment> findByBarberIdAndScheduledAtBetween(
      Long barberId, LocalDateTime start, LocalDateTime end);

  java.util.Optional<Appointment> findByBarberIdAndScheduledAt(
      Long barberId, LocalDateTime scheduledAt);
}
