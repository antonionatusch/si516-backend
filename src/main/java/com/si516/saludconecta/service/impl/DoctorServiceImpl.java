package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.dto.DoctorDTO;
import com.si516.saludconecta.mapper.DoctorMapper;
import com.si516.saludconecta.repository.DoctorRepository;
import com.si516.saludconecta.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;

    @Override
    public List<DoctorDTO> getAll() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorMapper::toDTO)
                .collect(Collectors.toList());
    }
}
