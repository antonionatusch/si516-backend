package com.si516.saludconecta.mapper;

import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.dto.DoctorDTO;

public class DoctorMapper {
    public static Doctor toEntity(DoctorDTO dto) {
        return new Doctor(
                dto.id(),
                dto.username(),
                null,
                dto.fullName(),
                dto.officeId()
        );
    }

    public static DoctorDTO toDTO(Doctor doctor) {
        return new DoctorDTO(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getFullName(),
                doctor.getOfficeId() != null ? doctor.getOfficeId() : null
        );
    }
}
