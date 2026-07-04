package com.project.back_end.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(
            AppointmentService appointmentService,
            Service service) {

        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{token}/{patientName}/{date}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String token,
            @PathVariable String patientName,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ResponseEntity<?> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return appointmentService.getAppointment(patientName, date, token);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = service.validateAppointment(appointment);

        if (result == -1) {
            return ResponseEntity.badRequest().body("Doctor not found");
        }

        if (result == 0) {
            return ResponseEntity.badRequest().body("Selected slot is unavailable");
        }

        return appointmentService.bookAppointment(appointment) == 1
                ? ResponseEntity.ok("Appointment booked successfully")
                : ResponseEntity.internalServerError().body("Failed to book appointment");
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}