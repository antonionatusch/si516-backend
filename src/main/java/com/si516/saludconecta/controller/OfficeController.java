package com.si516.saludconecta.controller;

import com.si516.saludconecta.dto.OfficeDTO;
import com.si516.saludconecta.service.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/offices")
@RequiredArgsConstructor
@CrossOrigin // opcional: ajusta orígenes según tu front
public class OfficeController {

    private final OfficeService officeService;

    /**
     * GET /offices  -> lista todas las oficinas.
     */
    @GetMapping
    public List<OfficeDTO> list() {
        return officeService.getAll();
    }

    /**
     * GET /offices/{id} -> retorna una oficina por id.
     */
    @GetMapping("/{id}")
    public OfficeDTO get(@PathVariable String id) {
        return officeService.getById(id);
    }

    /**
     * POST /offices -> crea una nueva oficina.
     * El campo id del DTO debe venir null (validado con @Null).
     */
    @PostMapping
    public ResponseEntity<OfficeDTO> create(@Valid @RequestBody OfficeDTO dto) {
        OfficeDTO created = officeService.create(dto);
        return ResponseEntity
                .created(URI.create("/offices/" + created.id()))
                .body(created);
    }

    /**
     * PUT /offices/{id} -> actualiza la oficina.
     * Ignora cualquier id dentro del DTO y usa el path variable.
     */
    @PutMapping("/{id}")
    public OfficeDTO update(@PathVariable String id,
                            @Valid @RequestBody OfficeDTO dto) {
        return officeService.update(id, dto);
    }

    /**
     * DELETE /offices/{id} -> elimina la oficina.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        officeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
