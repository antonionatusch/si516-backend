package com.si516.saludconecta.service;

import com.si516.saludconecta.dto.PatientDTO;

import java.util.List;

public interface PatientService {
    List<PatientDTO> getAll();

    PatientDTO getById(String id);

    PatientDTO getByName(String name);

    PatientDTO create(PatientDTO patient);

    PatientDTO update(String id, PatientDTO patient);

    void delete(String id);
}
