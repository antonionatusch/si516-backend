package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.document.ClinicHistory;
import com.si516.saludconecta.document.Prescription;
import com.si516.saludconecta.document.Treatment;
import com.si516.saludconecta.dto.ClinicHistoryDTO;
import com.si516.saludconecta.mapper.ClinicHistoryMapper;
import com.si516.saludconecta.repository.ClinicHistoryRepository;
import com.si516.saludconecta.service.ClinicHistoryService;
import com.si516.saludconecta.enums.PickupType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicHistoryServiceImpl implements ClinicHistoryService {

    private final ClinicHistoryRepository clinicHistoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ClinicHistoryDTO createFromTranscription(String audioId) {
        // Llamar al microservicio de transcripción
        String transcriptionUrl = "http://25.51.135.130:8001/transcribe/result/" + audioId;
        Map<String, Object> transcriptionJson = restTemplate.getForObject(transcriptionUrl, Map.class);

        if (transcriptionJson == null) {
            throw new RuntimeException("No se pudo obtener la transcripción del audio: " + audioId);
        }

        // Obtener el objeto 'extracted' que contiene los datos estructurados
        Map<String, Object> extractedData = (Map<String, Object>) transcriptionJson.get("extracted");

        if (extractedData == null) {
            throw new RuntimeException("No se encontraron datos extraídos en la transcripción: " + audioId);
        }

        var clinicHistory = new ClinicHistory();

        // Mapear campos básicos desde extractedData
        clinicHistory.setVisitReason((String) extractedData.get("visitReason"));
        clinicHistory.setDiagnosis((String) extractedData.get("diagnosis"));

        // Mapear symptoms con validación
        List<String> symptoms = (List<String>) extractedData.get("symptoms");
        clinicHistory.setSymptoms(symptoms != null ? symptoms : List.of());

        // Mapear treatment con validación
        List<Map<String, Object>> treatmentList = (List<Map<String, Object>>) extractedData.get("treatment");
        if (treatmentList != null && !treatmentList.isEmpty()) {
            List<Treatment> treatments = treatmentList.stream()
                    .map(treatmentMap -> objectMapper.convertValue(treatmentMap, Treatment.class))
                    .collect(Collectors.toList());
            clinicHistory.setTreatment(treatments);
        } else {
            clinicHistory.setTreatment(List.of());
        }

        // Mapear prescription con validación
        Map<String, Object> prescriptionMap = (Map<String, Object>) extractedData.get("prescription");
        if (prescriptionMap != null) {
            Prescription prescription = objectMapper.convertValue(prescriptionMap, Prescription.class);
            clinicHistory.setPrescription(prescription);
        } else {
            // Crear prescription por defecto usando el enum correcto
            clinicHistory.setPrescription(new Prescription(com.si516.saludconecta.enums.PickupType.LATER, null));
        }

        // Buscar patientId por nombre
        String patientName = (String) extractedData.get("patientName");
        String patientId = findPatientIdByName(patientName);
        clinicHistory.setPatientId(patientId);

        // Por ahora usar valores fijos para doctorId y officeId ya que no vienen en el JSON
        clinicHistory.setDoctorId("default-doctor");
        clinicHistory.setOfficeId("default-office");

        clinicHistory.setCreatedAt(Instant.now());

        var saved = clinicHistoryRepository.save(clinicHistory);
        return ClinicHistoryMapper.toDTO(saved);
    }

    private String findPatientIdByName(String patientName) {
        // Por ahora retorna null, después implementar búsqueda
        return null;
    }

    @Override
    public List<ClinicHistoryDTO> getAll() {
        return clinicHistoryRepository.findAll().stream()
                .map(ClinicHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClinicHistoryDTO getById(String id) {
        var clinicHistory = clinicHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClinicHistory not found: " + id));
        return ClinicHistoryMapper.toDTO(clinicHistory);
    }

    @Override
    public ClinicHistoryDTO create(ClinicHistoryDTO dto) {
        var clinicHistory = ClinicHistoryMapper.toEntity(dto);
        clinicHistory.setCreatedAt(Instant.now());
        var saved = clinicHistoryRepository.save(clinicHistory);
        return ClinicHistoryMapper.toDTO(saved);
    }

    @Override
    public ClinicHistoryDTO update(String id, ClinicHistoryDTO dto) {
        var existing = clinicHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClinicHistory not found: " + id));

        var updated = ClinicHistoryMapper.toEntity(dto);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());

        var saved = clinicHistoryRepository.save(updated);
        return ClinicHistoryMapper.toDTO(saved);
    }

    @Override
    public void delete(String id) {
        clinicHistoryRepository.deleteById(id);
    }
}