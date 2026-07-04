package com.project.back_end.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(
            PatientService patientService,
            Service service) {

        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<?> createPatient(
            @RequestBody Patient patient) {

        if (!service.validatePatient(patient)) {
            return ResponseEntity.badRequest()
                    .body("Patient already exists");
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {
            return ResponseEntity.ok("Patient registered successfully");
        }

        return ResponseEntity.internalServerError()
                .body("Failed to register patient");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Login login) {

        return service.validatePatientLogin(login);
    }

    @GetMapping("/appointments/{id}/{token}/{user}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token,
            @PathVariable String user) {

        ResponseEntity<?> validation = service.validateToken(token, user);

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/appointments/filter")
    public ResponseEntity<?> filterPatientAppointment(
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String name,
            @RequestParam String token) {

        ResponseEntity<?> validation = service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return service.filterPatient(condition, name, token);
    }
}