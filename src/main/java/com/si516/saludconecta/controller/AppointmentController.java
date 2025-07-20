package com.si516.saludconecta.controller;

import com.si516.saludconecta.dto.AppointmentDTO;
import com.si516.saludconecta.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@CrossOrigin
public class AppointmentController {
    private final AppointmentService appointmentService;

    // GET /appointments - List all appointments
    @GetMapping
    public List<AppointmentDTO> list() {
        return appointmentService.getAll();
    }

    // GET /appointments/{id} - Get an appointment by ID
    @GetMapping("/{id}")
    public AppointmentDTO getById(@PathVariable String id) {
        return appointmentService.getById(id);
    }

    // POST /appointments - Create a new appointment
    @PostMapping
    public ResponseEntity<AppointmentDTO> create(@Valid @RequestBody AppointmentDTO dto) {
        AppointmentDTO created = appointmentService.create(dto);
        return ResponseEntity
                .created(URI.create("/appointments/" + created.getAppointmentId()))
                .body(created);
    }

    // PUT /appointments/{id} - Update an appointment
    @PutMapping("/{id}")
    public AppointmentDTO update(@PathVariable String id,
                                 @Valid @RequestBody AppointmentDTO appointment) {
        return appointmentService.update(id, appointment);
    }

    // DELETE /appointments/{id} - Delete an appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}