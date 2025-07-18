package com.si516.saludconecta.dto;

import com.si516.saludconecta.document.Prescription;
import com.si516.saludconecta.document.Treatment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.List;

public record ClinicHistoryDTO (
        @Null(message = "Id must be null when creating") String id,
        @NotNull(message = "Doctor ID must not be null when creating") String doctorId,
        @NotNull(message = "Office ID must not be null when creating") String officeId,
        @NotNull(message = "Patient ID must not be null when creating") String patientId,
        @NotBlank(message = "Visit reason is required") String visitReason,
        @NotBlank(message = "Diagnosis is required") String diagnosis,
        @NotBlank(message = "Symptoms is required") List<String> symptoms,
        @NotBlank(message = "Treatment is required") List<Treatment> treatment,
        @NotBlank(message = "Prescription is required") Prescription prescription

) {
}
