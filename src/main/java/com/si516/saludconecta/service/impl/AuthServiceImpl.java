package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.document.Doctor;
import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.dto.AuthResponseDTO;
import com.si516.saludconecta.dto.DoctorDTO;
import com.si516.saludconecta.dto.LoginRequestDTO;
import com.si516.saludconecta.mapper.DoctorMapper;
import com.si516.saludconecta.repository.DoctorRepository;
import com.si516.saludconecta.repository.OfficeRepository;
import com.si516.saludconecta.service.AuthService;
import com.si516.saludconecta.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        // Find doctor by username
        Doctor doctor = doctorRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new RuntimeException("Doctor with username " + loginRequest.username() + " does not exist"));

        // Check password
        if (!passwordEncoder.matches(loginRequest.password(), doctor.getPasswordHash())) {
            throw new RuntimeException("Incorrect password");
        }

        // Generate JWT token
        String token = jwtUtil.createToken(doctor);

        return new AuthResponseDTO(
                token,
                doctor.getId(),
                doctor.getUsername(),
                doctor.getFullName(),
                doctor.getOffice().getId()
        );
    }

    @Override
    public DoctorDTO register(DoctorDTO doctorDTO) {
        // Check if doctor already exists
        if (doctorRepository.findByUsername(doctorDTO.username()).isPresent()) {
            throw new RuntimeException("Doctor with username " + doctorDTO.username() + " already exists");
        }

        // Validate that password is provided
        if (doctorDTO.password() == null || doctorDTO.password().trim().isEmpty()) {
            throw new RuntimeException("Password is required for registration");
        }

        // Find office
        Office office = officeRepository.findById(doctorDTO.officeId())
                .orElseThrow(() -> new RuntimeException("Office not found: " + doctorDTO.officeId()));

        // Create doctor with hashed password
        Doctor doctor = DoctorMapper.toEntity(doctorDTO, office);
        doctor.setPasswordHash(passwordEncoder.encode(doctorDTO.password()));

        Doctor saved = doctorRepository.save(doctor);
        return DoctorMapper.toDTO(saved);
    }
}