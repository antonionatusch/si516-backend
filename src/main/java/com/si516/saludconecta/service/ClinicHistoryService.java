package com.si516.saludconecta.service;

import com.si516.saludconecta.dto.ClinicHistoryDTO;
import java.util.List;

public interface ClinicHistoryService {
    List<ClinicHistoryDTO> getAll();
    ClinicHistoryDTO getById(String id);
    ClinicHistoryDTO create(ClinicHistoryDTO clinicHistory);
    ClinicHistoryDTO update(String id, ClinicHistoryDTO clinicHistory);
    void delete(String id);

    // Método actualizado para recibir solo el ID del audio
    ClinicHistoryDTO createFromTranscription(String audioId);
}