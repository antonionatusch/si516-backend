package com.si516.saludconecta.service.impl;

import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.dto.OfficeDTO;
import com.si516.saludconecta.mapper.OfficeMapper;
import com.si516.saludconecta.repository.OfficeRepository;
import com.si516.saludconecta.service.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;

    @Override
    public List<OfficeDTO> getAll() {
        return officeRepository.findAll().
                stream()
                .map(OfficeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OfficeDTO getById(String id) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Office not found:" + id));

        return OfficeMapper.toDTO(office);
    }

    @Override
    public OfficeDTO create(OfficeDTO officeDTO) {
        Office toSave = OfficeMapper.toEntity(officeDTO);
        Office saved = officeRepository.save(toSave);

        return OfficeMapper.toDTO(saved);
    }

    @Override
    public OfficeDTO update(String id, OfficeDTO officeDTO) {
        Office existing = officeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Office not found:" + id));

        existing.setCode(officeDTO.code());
        existing.setFloor(officeDTO.floor());

        Office updated = officeRepository.save(existing);
        return OfficeMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        officeRepository.deleteById(id);
    }
}
