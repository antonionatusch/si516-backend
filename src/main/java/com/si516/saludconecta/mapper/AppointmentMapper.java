package com.si516.saludconecta.mapper;

import com.si516.saludconecta.document.Appointment;
import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.document.Patient;
import com.si516.saludconecta.dto.AppointmentDTO;

public class AppointmentMapper {

    public static Appointment toEntity(AppointmentDTO dto, Office office, Doctor doctor, Patient patient) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(dto.getAppointmentId());
        appointment.setOffice(office); // Asignar el objeto Office completo
        appointment.setDoctor(doctor); // Asignar el objeto Doctor completo
        appointment.setPatient(patient); // Asignar el objeto Patient completo
        appointment.setAppointmentDate(dto.getAppointmentDate());
        return appointment;
    }

    public static AppointmentDTO toDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getAppointmentId(),
                appointment.getOffice().getId(),
                appointment.getDoctor().getId(),
                appointment.getPatient().getId(),
                appointment.getAppointmentDate()
        );
    }
}