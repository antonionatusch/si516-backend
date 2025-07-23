package com.si516.saludconecta.auth;

import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationLogicTest {

    private JwtUtil jwtUtil;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set private fields for testing
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "myTestSecretKey123456789012345678901234");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L); // 1 hour
        
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testPasswordEncoding() {
        // Test that passwords are properly encoded and can be verified
        String plainPassword = "testPassword123";
        
        String encodedPassword = passwordEncoder.encode(plainPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(plainPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(plainPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void testJwtTokenGeneration() {
        // Create test data
        Office office = new Office();
        office.setId("officeId123");
        office.setCode("OF001");
        office.setFloor(1);

        Doctor doctor = new Doctor();
        doctor.setId("doctorId123");
        doctor.setUsername("testdoctor");
        doctor.setFullName("Dr. Test Doctor");
        doctor.setOffice(office);
        doctor.setPasswordHash(passwordEncoder.encode("password123"));

        // Generate JWT token
        String token = jwtUtil.createToken(doctor);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void testJwtTokenValidation() {
        // Create test data
        Office office = new Office();
        office.setId("officeId123");
        office.setCode("OF001");
        office.setFloor(1);

        Doctor doctor = new Doctor();
        doctor.setId("doctorId123");
        doctor.setUsername("testdoctor");
        doctor.setFullName("Dr. Test Doctor");
        doctor.setOffice(office);

        // Generate and validate token
        String token = jwtUtil.createToken(doctor);
        
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals("doctorId123", jwtUtil.validateTokenAndGetSubject(token));
    }

    @Test
    void testInvalidJwtToken() {
        String invalidToken = "invalid.token.here";
        
        assertFalse(jwtUtil.isTokenValid(invalidToken));
        assertThrows(RuntimeException.class, () -> {
            jwtUtil.validateTokenAndGetSubject(invalidToken);
        });
    }
}