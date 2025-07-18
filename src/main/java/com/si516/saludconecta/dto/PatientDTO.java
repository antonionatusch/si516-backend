package com.si516.saludconecta.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientDTO(
        @Null(message = "Id must be null when creating") String id,
        @NotBlank(message = "Name is required") String name,
        @Past(message = "Date of birth must be in the past") LocalDate dob,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^\\d{7}$", message = "Phone must be exactly 7 digits")
        String phone
) {
}
