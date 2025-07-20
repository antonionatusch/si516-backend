package com.si516.saludconecta.controller;

import com.si516.saludconecta.dto.file.FileMetadataDTO;
import com.si516.saludconecta.dto.file.FileUploadResponseDTO;
import com.si516.saludconecta.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponseDTO> uploadAudio(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String patientId
    ) throws IOException {

        var meta = fileStorageService.storeAudio(file, doctorId, patientId);
        return ResponseEntity.ok(new FileUploadResponseDTO(
                meta.id(),
                meta.filename(),
                meta.contentType(),
                meta.length()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        var resourceOpt = fileStorageService.loadAsResource(id);
        if (resourceOpt.isEmpty()) return ResponseEntity.notFound().build();

        var metaOpt = fileStorageService.getMetadata(id);
        String contentType = metaOpt.map(FileMetadataDTO::contentType).orElse("application/octet-stream");
        String filename = metaOpt.map(FileMetadataDTO::filename).orElse(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resourceOpt.get());
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<FileMetadataDTO> metadata(@PathVariable String id) {
        return fileStorageService.getMetadata(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<FileMetadataDTO> list(@RequestParam(defaultValue = "20") int limit) {
        return fileStorageService.list(Math.min(limit, 100));
    }
}
