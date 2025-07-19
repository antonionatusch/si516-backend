package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.dto.file.FileMetadataDTO;
import com.si516.saludconecta.event.NewAudioStoredEvent;
import com.si516.saludconecta.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public FileMetadataDTO storeAudio(MultipartFile file,
                                      String doctorId,
                                      String patientId,
                                      String appointmentId) throws IOException {

        Document meta = new Document();
        meta.put("doctorId", doctorId);
        meta.put("patientId", patientId);
        meta.put("appointmentId", appointmentId);
        meta.put("originalFilename", file.getOriginalFilename());
        meta.put("contentType", file.getContentType());

        var objectId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                meta
        );

        GridFSFile storedFile = gridFsTemplate.findOne(query(where("_id").is(objectId.toHexString())));

        FileMetadataDTO dto = new FileMetadataDTO(
                objectId.toHexString(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                storedFile.getUploadDate().toInstant(),
                meta
        );

        eventPublisher.publishEvent(new NewAudioStoredEvent(this, dto.id(), dto.filename()));

        return dto;
    }

    @Override
    public Optional<Resource> loadAsResource(String fileId) {
        GridFSFile gfsFile = gridFsTemplate.findOne(query(where("_id").is(fileId)));
        GridFsResource res = gridFsTemplate.getResource(gfsFile);
        try {
            return Optional.of(new InputStreamResource(res.getInputStream()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<FileMetadataDTO> getMetadata(String fileId) {
        GridFSFile f = gridFsTemplate.findOne(query(where("_id").is(fileId)));
        return Optional.of(extractMetadata(f));
    }

    @Override
    public List<FileMetadataDTO> list(int limit) {
        var iterable = gridFsTemplate.find(
                new org.springframework.data.mongodb.core.query.Query()
                        .limit(limit)
                        .with(Sort.by(Sort.Direction.DESC, "uploadDate"))
        );
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(this::extractMetadata)
                .toList();
    }

    private FileMetadataDTO extractMetadata(GridFSFile f) {
        Map<String, Object> meta = f.getMetadata() != null ? f.getMetadata() : Collections.emptyMap();
        String contentType = meta.getOrDefault("contentType", "application/octet-stream").toString();
        return new FileMetadataDTO(
                f.getObjectId().toHexString(),
                f.getFilename(),
                contentType,
                f.getLength(),
                f.getUploadDate().toInstant(),
                meta
        );
    }
}
