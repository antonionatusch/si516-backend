package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.document.Patient;
import com.si516.saludconecta.dto.PatientDTO;
import com.si516.saludconecta.mapper.PatientMapper;
import com.si516.saludconecta.repository.PatientRepository;
import com.si516.saludconecta.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;


    @Override
    public List<PatientDTO> getAll() {
        return patientRepository.findAll().
                stream()
                .map(PatientMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDTO getById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + id));
        return PatientMapper.toDTO(patient);
    }

    @Override
    public PatientDTO create(PatientDTO patientDTO) {
        Patient toSave = PatientMapper.toEntity(patientDTO);
        Patient saved = patientRepository.save(toSave);
        return PatientMapper.toDTO(saved);
    }

    @Override
    public PatientDTO update(String id, PatientDTO patientDTO) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + id));
        // actualizar campos
        existing.setName(patientDTO.name());
        existing.setDob(patientDTO.dob());
        existing.setEmail(patientDTO.email());
        existing.setPhone(patientDTO.phone());
        Patient updated = patientRepository.save(existing);
        return PatientMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        patientRepository.deleteById(id);
    }
}
