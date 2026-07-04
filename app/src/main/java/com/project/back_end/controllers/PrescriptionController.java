package com.project.back_end.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(
            PrescriptionService prescriptionService,
            Service service,
            AppointmentService appointmentService) {

        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {

        ResponseEntity<?> validation = service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        appointmentService.changeStatus(
                prescription.getAppointmentId(), 1);

        boolean saved =
                prescriptionService.savePrescription(prescription);

        if (saved) {
            return ResponseEntity.ok("Prescription saved successfully");
        }

        return ResponseEntity.badRequest()
                .body("Prescription already exists");
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<?> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        Prescription prescription =
                prescriptionService.getPrescription(appointmentId);

        if (prescription == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(prescription);
    }
}