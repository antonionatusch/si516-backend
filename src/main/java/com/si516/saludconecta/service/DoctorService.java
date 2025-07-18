package com.si516.saludconecta.service;

import com.si516.saludconecta.dto.DoctorDTO;
import java.util.List;

public interface DoctorService {
    List<DoctorDTO> getAll();
    DoctorDTO getById(String id);
    DoctorDTO create(DoctorDTO patient);
    DoctorDTO update(String id, DoctorDTO patient);
    void delete(String id);
}
