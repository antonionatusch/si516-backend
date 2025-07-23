package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.dto.DoctorDTO;
import com.si516.saludconecta.mapper.DoctorMapper;
import com.si516.saludconecta.repository.DoctorRepository;
import com.si516.saludconecta.repository.OfficeRepository;
import com.si516.saludconecta.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<DoctorDTO> getAll() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDTO getById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
        return DoctorMapper.toDTO(doctor);
    }

    @Override
    public DoctorDTO create(DoctorDTO doctorDTO) {
        Office office = officeRepository.findById(doctorDTO.officeId())
                .orElseThrow(() -> new RuntimeException("Office not found: " + doctorDTO.officeId()));

        Doctor doctor = DoctorMapper.toEntity(doctorDTO, office);
        
        // Hash password if provided
        if (doctorDTO.password() != null && !doctorDTO.password().trim().isEmpty()) {
            doctor.setPasswordHash(passwordEncoder.encode(doctorDTO.password()));
        }
        
        Doctor saved = doctorRepository.save(doctor);
        return DoctorMapper.toDTO(saved);
    }

    @Override
    public DoctorDTO update(String id, DoctorDTO doctorDTO) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));

        Office office = officeRepository.findById(doctorDTO.officeId())
                .orElseThrow(() -> new RuntimeException("Office not found: " + doctorDTO.officeId()));

        existing.setUsername(doctorDTO.username());
        existing.setFullName(doctorDTO.fullName());
        existing.setOffice(office);
        
        // Update password if provided
        if (doctorDTO.password() != null && !doctorDTO.password().trim().isEmpty()) {
            existing.setPasswordHash(passwordEncoder.encode(doctorDTO.password()));
        }

        Doctor updated = doctorRepository.save(existing);
        return DoctorMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        doctorRepository.deleteById(id);
    }
}
