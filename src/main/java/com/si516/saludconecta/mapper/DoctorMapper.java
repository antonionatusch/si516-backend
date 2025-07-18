package com.si516.saludconecta.mapper;

import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.dto.DoctorDTO;

public class DoctorMapper {
    public static Doctor toEntity(DoctorDTO dto, Office office) {
        Doctor doctor = new Doctor();
        doctor.setId(dto.id());
        doctor.setUsername(dto.username());
        doctor.setFullName(dto.fullName());
        doctor.setOffice(office); // Asignar el objeto Office completo
        return doctor;
    }

    public static DoctorDTO toDTO(Doctor doctor) {
        return new DoctorDTO(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getFullName(),

                doctor.getOffice().getId()

        );
    }
}
