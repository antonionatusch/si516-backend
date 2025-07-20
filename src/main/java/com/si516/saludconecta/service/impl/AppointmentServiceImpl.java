package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.document.Appointment;
import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.document.Patient;
import com.si516.saludconecta.dto.AppointmentDTO;
import com.si516.saludconecta.mapper.AppointmentMapper;
import com.si516.saludconecta.repository.AppointmentRepository;
import com.si516.saludconecta.repository.DoctorRepository;
import com.si516.saludconecta.repository.OfficeRepository;
import com.si516.saludconecta.repository.PatientRepository;
import com.si516.saludconecta.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final OfficeRepository officeRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public List<AppointmentDTO> getAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO getById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
        return AppointmentMapper.toDTO(appointment);
    }

    @Override
    public AppointmentDTO create(AppointmentDTO appointmentDTO) {
        Office office = officeRepository.findById(appointmentDTO.getOfficeId())
                .orElseThrow(() -> new RuntimeException("Office not found: " + appointmentDTO.getOfficeId()));

        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + appointmentDTO.getDoctorId()));

        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + appointmentDTO.getPatientId()));

        Appointment appointment = AppointmentMapper.toEntity(appointmentDTO, office, doctor, patient);
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentMapper.toDTO(saved);
    }

    @Override
    public AppointmentDTO update(String id, AppointmentDTO appointmentDTO) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));

        Office office = officeRepository.findById(appointmentDTO.getOfficeId())
                .orElseThrow(() -> new RuntimeException("Office not found: " + appointmentDTO.getOfficeId()));

        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + appointmentDTO.getDoctorId()));

        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + appointmentDTO.getPatientId()));

        existing.setOffice(office);
        existing.setDoctor(doctor);
        existing.setPatient(patient);
        existing.setAppointmentDate(appointmentDTO.getAppointmentDate());

        Appointment updated = appointmentRepository.save(existing);
        return AppointmentMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        appointmentRepository.deleteById(id);
    }
}