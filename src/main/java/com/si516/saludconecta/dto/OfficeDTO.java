package com.si516.saludconecta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record OfficeDTO(
        @Null(message = "Id must be null when creating") String id,
        @NotBlank(message = "Code is required") String code,
        @NotNull(message = "Floor is required") Integer floor
) {
}
