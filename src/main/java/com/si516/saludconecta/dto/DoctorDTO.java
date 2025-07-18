package com.si516.saludconecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record DoctorDTO (
        @Null(message = "Id must be null when creating") String id,
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Full name is required") String fullName,
        @NotNull(message = "Office ID must not be null when creating") String officeId
) {
}
