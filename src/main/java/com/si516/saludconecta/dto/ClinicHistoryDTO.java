package com.si516.saludconecta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.si516.saludconecta.document.Pickup;
import com.si516.saludconecta.document.Treatment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record ClinicHistoryDTO(
        @Null(message = "Id must be null when creating") String id,
        @NotNull(message = "Doctor ID must not be null when creating") String doctorId,
        @NotNull(message = "Office ID must not be null when creating") String officeId,
        @NotNull(message = "Patient ID must not be null when creating") String patientId,
        @NotBlank(message = "Visit reason is required") String visitReason,
        @NotBlank(message = "Diagnosis is required") String diagnosis,
        @NotNull(message = "Symptoms must not be null") @Size(min = 1, message = "Symptoms must contain at least one item") List<String> symptoms,
        @NotNull(message = "Treatment must not be null") @Size(min = 1, message = "Treatment must contain at least one item") List<Treatment> treatment,
        @NotNull(message = "Pickup is required") Pickup pickup,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "dd-MM-yyyy HH:mm:ss",
                timezone = "America/La_Paz")
        @NotNull(message = "createdAt is required") Instant createdAt

) {
}
