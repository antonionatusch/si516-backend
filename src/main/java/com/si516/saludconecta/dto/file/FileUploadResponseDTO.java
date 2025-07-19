package com.si516.saludconecta.dto.file;

public record FileUploadResponseDTO(
        String fileId,
        String filename,
        String contentType,
        long size
) {}
