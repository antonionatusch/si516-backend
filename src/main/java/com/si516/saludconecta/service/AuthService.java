package com.si516.saludconecta.service;

import com.si516.saludconecta.dto.AuthResponseDTO;
import com.si516.saludconecta.dto.DoctorDTO;
import com.si516.saludconecta.dto.LoginRequestDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO loginRequest);
    DoctorDTO register(DoctorDTO doctorDTO);
}