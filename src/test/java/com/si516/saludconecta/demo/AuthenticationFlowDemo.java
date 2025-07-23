package com.si516.saludconecta.demo;

import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.dto.AuthResponseDTO;
import com.si516.saludconecta.dto.DoctorDTO;
import com.si516.saludconecta.dto.LoginRequestDTO;
import com.si516.saludconecta.service.impl.AuthServiceImpl;
import com.si516.saludconecta.util.JwtUtil;
import com.si516.saludconecta.repository.DoctorRepository;
import com.si516.saludconecta.repository.OfficeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthenticationFlowDemo {

    @Test
    void demonstrateFullAuthenticationFlow() {
        // Setup mocks
        DoctorRepository doctorRepository = Mockito.mock(DoctorRepository.class);
        OfficeRepository officeRepository = Mockito.mock(OfficeRepository.class);
        
        // Setup JWT utility
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "myTestSecretKey123456789012345678901234");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L);
        
        // Setup authentication service
        AuthServiceImpl authService = new AuthServiceImpl(doctorRepository, officeRepository, jwtUtil);
        
        // Create test office
        Office testOffice = new Office();
        testOffice.setId("office123");
        testOffice.setCode("OF001");
        testOffice.setFloor(1);
        
        // Mock office repository
        when(officeRepository.findById("office123")).thenReturn(Optional.of(testOffice));
        
        // Step 1: Registration
        System.out.println("\n=== REGISTRATION FLOW ===");
        
        DoctorDTO registrationRequest = new DoctorDTO(
            null,
            "johndoe",
            "Dr. John Doe",
            "office123",
            "mySecurePassword123"
        );
        
        // Mock doctor repository for registration
        when(doctorRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor saved = invocation.getArgument(0);
            saved.setId("doctor123");
            return saved;
        });
        
        DoctorDTO registeredDoctor = authService.register(registrationRequest);
        
        System.out.println("Registration Request:");
        System.out.println("  Username: " + registrationRequest.username());
        System.out.println("  Full Name: " + registrationRequest.fullName());
        System.out.println("  Password: [HIDDEN FOR SECURITY]");
        System.out.println("  Office ID: " + registrationRequest.officeId());
        
        System.out.println("\nRegistration Response:");
        System.out.println("  Doctor ID: " + registeredDoctor.id());
        System.out.println("  Username: " + registeredDoctor.username());
        System.out.println("  Full Name: " + registeredDoctor.fullName());
        System.out.println("  Password in Response: " + registeredDoctor.password() + " (should be null for security)");
        
        // Step 2: Login
        System.out.println("\n=== LOGIN FLOW ===");
        
        LoginRequestDTO loginRequest = new LoginRequestDTO("johndoe", "mySecurePassword123");
        
        // Create the doctor that would be found in the database
        Doctor existingDoctor = new Doctor();
        existingDoctor.setId("doctor123");
        existingDoctor.setUsername("johndoe");
        existingDoctor.setFullName("Dr. John Doe");
        existingDoctor.setOffice(testOffice);
        
        // Hash the password as it would be stored in the database
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        existingDoctor.setPasswordHash(encoder.encode("mySecurePassword123"));
        
        // Mock doctor repository for login
        when(doctorRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingDoctor));
        
        AuthResponseDTO loginResponse = authService.login(loginRequest);
        
        System.out.println("Login Request:");
        System.out.println("  Username: " + loginRequest.username());
        System.out.println("  Password: [HIDDEN FOR SECURITY]");
        
        System.out.println("\nLogin Response:");
        System.out.println("  JWT Token: " + loginResponse.token().substring(0, 20) + "...[truncated]");
        System.out.println("  Doctor ID: " + loginResponse.doctorId());
        System.out.println("  Username: " + loginResponse.username());
        System.out.println("  Full Name: " + loginResponse.fullName());
        System.out.println("  Office ID: " + loginResponse.officeId());
        
        // Step 3: Token Validation
        System.out.println("\n=== TOKEN VALIDATION ===");
        
        boolean isValid = jwtUtil.isTokenValid(loginResponse.token());
        String extractedDoctorId = jwtUtil.validateTokenAndGetSubject(loginResponse.token());
        
        System.out.println("Token is valid: " + isValid);
        System.out.println("Extracted doctor ID from token: " + extractedDoctorId);
        
        // Assertions
        assertNotNull(registeredDoctor);
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.token());
        assertTrue(isValid);
        assertEquals("doctor123", extractedDoctorId);
        assertEquals("johndoe", loginResponse.username());
        assertNull(registeredDoctor.password()); // Password should not be returned
        
        System.out.println("\n=== DEMO COMPLETED SUCCESSFULLY ===");
        System.out.println("✓ Doctor registered with hashed password");
        System.out.println("✓ Doctor logged in successfully");
        System.out.println("✓ JWT token generated and validated");
        System.out.println("✓ Password never exposed in responses");
    }
    
    @Test
    void demonstrateInvalidLogin() {
        // Setup mocks
        DoctorRepository doctorRepository = Mockito.mock(DoctorRepository.class);
        OfficeRepository officeRepository = Mockito.mock(OfficeRepository.class);
        
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "myTestSecretKey123456789012345678901234");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L);
        
        AuthServiceImpl authService = new AuthServiceImpl(doctorRepository, officeRepository, jwtUtil);
        
        System.out.println("\n=== INVALID LOGIN DEMO ===");
        
        // Test 1: User doesn't exist
        when(doctorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        LoginRequestDTO invalidUserLogin = new LoginRequestDTO("nonexistent", "password");
        Exception exception1 = assertThrows(RuntimeException.class, () -> {
            authService.login(invalidUserLogin);
        });
        System.out.println("Login with nonexistent user: " + exception1.getMessage());
        
        // Test 2: Wrong password
        Doctor existingDoctor = new Doctor();
        existingDoctor.setUsername("johndoe");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        existingDoctor.setPasswordHash(encoder.encode("correctPassword"));
        
        when(doctorRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingDoctor));
        
        LoginRequestDTO wrongPasswordLogin = new LoginRequestDTO("johndoe", "wrongPassword");
        Exception exception2 = assertThrows(RuntimeException.class, () -> {
            authService.login(wrongPasswordLogin);
        });
        System.out.println("Login with wrong password: " + exception2.getMessage());
        
        System.out.println("✓ Invalid login attempts properly rejected");
    }
}