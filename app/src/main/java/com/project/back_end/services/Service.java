package com.project.back_end.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService) {

        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String role) {

        Map<String, String> response = new HashMap<>();

        if (!tokenService.validateToken(token, role)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {

        Map<String, String> response = new HashMap<>();

        try {

            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null ||
                !admin.getPassword().equals(receivedAdmin.getPassword())) {

                response.put("message", "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token =
                    tokenService.generateToken(admin.getUsername(), "admin");

            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(
            String name,
            String specialty,
            String time) {

        if (name != null && specialty != null && time != null)
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);

        if (name != null && specialty != null)
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);

        if (name != null && time != null)
            return doctorService.filterDoctorByNameAndTime(name, time);

        if (specialty != null && time != null)
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);

        if (name != null)
            return doctorService.findDoctorByName(name);

        if (specialty != null)
            return doctorService.filterDoctorBySpecility(specialty);

        if (time != null)
            return doctorService.filterDoctorsByTime(time);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());

        return response;
    }

    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> doctorOpt =
                doctorRepository.findById(appointment.getDoctor().getId());

        if (doctorOpt.isEmpty()) {
            return -1;
        }

        LocalDate date = appointment.getAppointmentTime().toLocalDate();

        List<String> availableSlots =
                doctorService.getDoctorAvailability(
                        doctorOpt.get().getId(),
                        date);

        String requestedTime =
                appointment.getAppointmentTime()
                        .toLocalTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        for (String slot : availableSlots) {

            String startTime = slot.split("-")[0];

            if (startTime.equals(requestedTime)) {
                return 1;
            }
        }

        return 0;
    }

    public boolean validatePatient(Patient patient) {

        Patient existing =
                patientRepository.findByEmailOrPhone(
                        patient.getEmail(),
                        patient.getPhone());

        return existing == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        try {

            Patient patient =
                    patientRepository.findByEmail(login.getIdentifier());

            if (patient == null ||
                    !patient.getPassword().equals(login.getPassword())) {

                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token =
                    tokenService.generateToken(patient.getEmail(), "patient");

            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token) {

        String email = tokenService.extractIdentifier(token);

        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Unauthorized");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long patientId = patient.getId();

        if ("null".equalsIgnoreCase(condition) || "all".equalsIgnoreCase(condition))
            condition = null;

        if ("null".equalsIgnoreCase(name) || "all".equalsIgnoreCase(name))
            name = null;

        if (condition != null && name != null)
            return patientService.filterByDoctorAndCondition(condition, name, patientId);

        if (condition != null)
            return patientService.filterByCondition(condition, patientId);

        if (name != null)
            return patientService.filterByDoctor(name, patientId);

        return patientService.getPatientAppointment(patientId, token);
    }
}