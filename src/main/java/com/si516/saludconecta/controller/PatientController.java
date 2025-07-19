package com.si516.saludconecta.controller;

import com.si516.saludconecta.dto.PatientDTO;
import com.si516.saludconecta.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@CrossOrigin // opcional: ajusta origins si tu front corre en otro puerto
public class PatientController {

    private final PatientService patientService;

    /**
     * GET /patients : lista todos los pacientes
     */
    @GetMapping
    public List<PatientDTO> list() {
        return patientService.getAll();
    }

    /**
     * GET /patients/{id} : obtiene un paciente por id
     */
    @GetMapping("/{id}")
    public PatientDTO get(@PathVariable String id) {
        return patientService.getById(id);
    }

    /**
     * POST /patients : crea un paciente.
     * El campo id debe venir null (validado con @Null en el DTO).
     */
    @PostMapping
    public ResponseEntity<PatientDTO> create(@Valid @RequestBody PatientDTO dto) {
        PatientDTO created = patientService.create(dto);
        return ResponseEntity
                .created(URI.create("/patients/" + created.id()))
                .body(created);
    }

    /**
     * PUT /patients/{id} : actualiza un paciente.
     * Ignora cualquier id en el DTO (se usa el path variable).
     */
    @PutMapping("/{id}")
    public PatientDTO update(@PathVariable String id,
                             @Valid @RequestBody PatientDTO dto) {
        return patientService.update(id, dto);
    }

    /**
     * DELETE /patients/{id} : elimina un paciente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
