package com.si516.saludconecta.mapper;

// Line removed as it is unused.
import com.si516.saludconecta.document.Patient;
// Line removed as it is unused.
import com.si516.saludconecta.dto.PatientDTO;

public class PatientMapper {

    public static Patient toEntity(PatientDTO dto) {
        return new Patient(
                dto.id(),    // null al crear; valor existente al actualizar
                dto.name(),
                dto.dob(),
                dto.email(),
                dto.phone()
        );
    }

    public static PatientDTO toDTO(Patient patient) {
        return new PatientDTO(
                patient.getId(),
                patient.getName(),
                patient.getDob(),
                patient.getEmail(),
                patient.getPhone()
        );
    }
}
