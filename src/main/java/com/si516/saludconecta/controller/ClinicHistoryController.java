package com.si516.saludconecta.controller;

import com.si516.saludconecta.dto.ClinicHistoryDTO;
import com.si516.saludconecta.service.ClinicHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clinic-histories")
@RequiredArgsConstructor
@CrossOrigin
public class ClinicHistoryController {

    private final ClinicHistoryService clinicHistoryService;

    @GetMapping
    public List<ClinicHistoryDTO> getAll() {
        return clinicHistoryService.getAll();
    }

    @GetMapping("/{id}")
    public ClinicHistoryDTO getById(@PathVariable String id) {
        return clinicHistoryService.getById(id);
    }

    @PostMapping
    public ClinicHistoryDTO create(@RequestBody ClinicHistoryDTO dto) {
        return clinicHistoryService.create(dto);
    }

    @PostMapping("/from-transcription/{audioId}")
    public ClinicHistoryDTO createFromTranscription(@PathVariable String audioId) {
        return clinicHistoryService.createFromTranscription(audioId);
    }

    @PutMapping("/{id}")
    public ClinicHistoryDTO update(@PathVariable String id, @RequestBody ClinicHistoryDTO dto) {
        return clinicHistoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        clinicHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}