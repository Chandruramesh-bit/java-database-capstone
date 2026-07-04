package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt =
                appointmentRepository.findById(appointment.getId());

        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();

        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());

        appointmentRepository.save(existing);

        response.put("message", "Appointment updated successfully.");
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        if (!tokenService.validateToken(token, "patient")) {
            response.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        appointmentRepository.delete(appointmentOpt.get());

        response.put("message", "Appointment cancelled successfully.");
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAppointment(
            String patientName,
            LocalDate date,
            String token) {

        String email = tokenService.extractIdentifier(token);

        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
        }

        Long doctorId = doctor.getId();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> appointments;

        if (patientName != null
                && !patientName.isBlank()
                && !"null".equalsIgnoreCase(patientName)) {

            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, patientName, start, end);

        } else {

            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(
                            doctorId, start, end);
        }

        return ResponseEntity.ok(Map.of("appointments", appointments));
    }

    @Transactional
    public void changeStatus(long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}