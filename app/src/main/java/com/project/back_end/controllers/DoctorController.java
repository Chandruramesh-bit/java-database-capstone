package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(
            DoctorService doctorService,
            Service service) {

        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, user);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return ResponseEntity.ok(
                Map.of("availableSlots",
                        doctorService.getDoctorAvailability(doctorId, date)));
    }

    @GetMapping
    public ResponseEntity<?> getDoctor() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == -1) {
            return ResponseEntity.status(409).body("Doctor already exists");
        }

        if (result == 1) {
            return ResponseEntity.ok("Doctor added successfully");
        }

        return ResponseEntity.internalServerError().body("Failed to add doctor");
    }

    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(
            @RequestBody Login login) {

        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == -1) {
            return ResponseEntity.badRequest().body("Doctor not found");
        }

        if (result == 1) {
            return ResponseEntity.ok("Doctor updated successfully");
        }

        return ResponseEntity.internalServerError().body("Failed to update doctor");
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        int result = doctorService.deleteDoctor(id);

        if (result == -1) {
            return ResponseEntity.badRequest().body("Doctor not found");
        }

        if (result == 1) {
            return ResponseEntity.ok("Doctor deleted successfully");
        }

        return ResponseEntity.internalServerError().body("Failed to delete doctor");
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String time) {

        return ResponseEntity.ok(
                service.filterDoctor(name, specialty, time));
    }
}