package com.si516.saludconecta.dto.file;

import java.time.Instant;
import java.util.Map;

public record FileMetadataDTO(
        String id,
        String filename,
        String contentType,
        long length,
        Instant uploadDate,
        Map<String, Object> metadata
) {}
