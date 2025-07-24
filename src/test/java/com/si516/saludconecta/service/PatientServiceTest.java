package com.si516.saludconecta.service;

import com.si516.saludconecta.document.Patient;
import com.si516.saludconecta.dto.PatientDTO;
import com.si516.saludconecta.repository.PatientRepository;
import com.si516.saludconecta.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient testPatient;
    private final String TEST_NAME = "John Doe";
    private final String TEST_ID = "patient123";

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(TEST_ID);
        testPatient.setName(TEST_NAME);
        testPatient.setDob(LocalDate.of(1990, 1, 1));
        testPatient.setEmail("john.doe@example.com");
        testPatient.setPhone("12345678");
    }

    @Test
    void getByName_WhenPatientExists_ReturnsPatientDTO() {
        // Given
        when(patientRepository.findByName(TEST_NAME)).thenReturn(Optional.of(testPatient));

        // When
        PatientDTO result = patientService.getByName(TEST_NAME);

        // Then
        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals(TEST_NAME, result.name());
        assertEquals(LocalDate.of(1990, 1, 1), result.dob());
        assertEquals("john.doe@example.com", result.email());
        assertEquals("12345678", result.phone());
        
        verify(patientRepository).findByName(TEST_NAME);
    }

    @Test
    void getByName_WhenPatientDoesNotExist_ThrowsRuntimeException() {
        // Given
        when(patientRepository.findByName(TEST_NAME)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> patientService.getByName(TEST_NAME));
        
        assertTrue(exception.getMessage().contains("Patient not found with name: " + TEST_NAME));
        verify(patientRepository).findByName(TEST_NAME);
    }
}