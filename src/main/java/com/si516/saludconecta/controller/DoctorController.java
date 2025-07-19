package com.si516.saludconecta.controller;

import com.si516.saludconecta.dto.DoctorDTO;
import com.si516.saludconecta.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@CrossOrigin
public class DoctorController {
    private final DoctorService doctorService;


    // GET /doctors  - List all doctors
    @GetMapping
    public List<DoctorDTO> list() {
        return doctorService.getAll();
    }

    // GET /doctors/{id} - Get a doctor by ID
     @GetMapping("/{id}")
     public DoctorDTO getById(@PathVariable String id) {
         return doctorService.getById(id);
    }

    // POST /doctors - Create a new doctor
    @PostMapping
    public ResponseEntity<DoctorDTO> create(@Valid @RequestBody DoctorDTO dto) {
        DoctorDTO created = doctorService.create(dto);
        return ResponseEntity
                .created(URI.create("/doctors/" + created.id()))
                .body(created);
    }

    // PUT /doctors/{id} - Update a doctor
    @PutMapping("/{id}")
    public DoctorDTO update(@PathVariable String id,
                            @Valid @RequestBody DoctorDTO doctor) {
        return doctorService.update(id, doctor);
    }

    // DELETE /doctors/{id} - Delete a doctor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        // Eliminar el doctor directamente
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
