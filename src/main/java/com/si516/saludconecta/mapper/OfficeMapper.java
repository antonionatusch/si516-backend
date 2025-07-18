package com.si516.saludconecta.mapper;

import com.si516.saludconecta.document.Office;
import com.si516.saludconecta.dto.OfficeDTO;

public class OfficeMapper {
    public static Office toEntity(OfficeDTO dto) {
        return new Office(
                dto.id(),    // null al crear; valor existente al actualizar
                dto.code(),
                dto.floor()
        );
    }

    public static OfficeDTO toDTO(Office office) {
        return new OfficeDTO(
                office.getId(),
                office.getCode(),
                office.getFloor()
        );
    }
}
