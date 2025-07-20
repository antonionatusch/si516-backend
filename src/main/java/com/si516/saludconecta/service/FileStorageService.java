package com.si516.saludconecta.service;

import com.si516.saludconecta.dto.file.FileMetadataDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileStorageService {
    FileMetadataDTO storeAudio(MultipartFile file,
                               String doctorId,
                               String patientId) throws IOException;

    Optional<Resource> loadAsResource(String fileId);

    Optional<FileMetadataDTO> getMetadata(String fileId);

    List<FileMetadataDTO> list(int limit);
}
