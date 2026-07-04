package com.project.back_end.services;
import javax.crypto.SecretKey;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {

        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /* ============================================================
       SIGNING KEY
       ============================================================ */

       private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /* ============================================================
       GENERATE TOKEN
       ============================================================ */

    public String generateToken(String identifier, String role) {

        return Jwts.builder()
                .setSubject(identifier)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /* ============================================================
       EXTRACT IDENTIFIER
       ============================================================ */

       public String extractIdentifier(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    
        return claims.getSubject();
    }
    /* ============================================================
       VALIDATE TOKEN
       ============================================================ */

    public boolean validateToken(String token, String role) {

        try {

            String identifier = extractIdentifier(token);

            switch (role.toLowerCase()) {

                case "admin":
                    Admin admin = adminRepository.findByUsername(identifier);
                    return admin != null;

                case "doctor":
                    Doctor doctor = doctorRepository.findByEmail(identifier);
                    return doctor != null;

                case "patient":
                    Patient patient = patientRepository.findByEmail(identifier);
                    return patient != null;

                default:
                    return false;
            }

        } catch (Exception e) {
            return false;
        }
    }
}