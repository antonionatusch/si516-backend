package com.si516.saludconecta.service;

import com.si516.saludconecta.dto.AppointmentDTO;
import java.util.List;

public interface AppointmentService {
    List<AppointmentDTO> getAll();
    AppointmentDTO getById(String id);
    AppointmentDTO create(AppointmentDTO appointment);
    AppointmentDTO update(String id, AppointmentDTO appointment);
    void delete(String id);
}