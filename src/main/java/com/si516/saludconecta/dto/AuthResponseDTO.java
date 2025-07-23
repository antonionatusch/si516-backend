package com.si516.saludconecta.dto;

public record AuthResponseDTO(
        String token,
        String doctorId,
        String username,
        String fullName,
        String officeId
) {
}