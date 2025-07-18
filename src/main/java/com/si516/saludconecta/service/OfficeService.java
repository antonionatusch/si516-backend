package com.si516.saludconecta.service;

import com.si516.saludconecta.dto.OfficeDTO;

import java.util.List;

public interface OfficeService {
    List<OfficeDTO> getAll();

    OfficeDTO getById(String id);

    OfficeDTO create(OfficeDTO office);

    OfficeDTO update(String id, OfficeDTO office);

    void delete(String id);
}
