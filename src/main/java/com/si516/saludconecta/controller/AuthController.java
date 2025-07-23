package com.si516.saludconecta.controller;

import com.si516.saludconecta.dto.AuthResponseDTO;
import com.si516.saludconecta.dto.DoctorDTO;
import com.si516.saludconecta.dto.LoginRequestDTO;
import com.si516.saludconecta.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            AuthResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            pd.setTitle("Login failed");
            pd.setDetail(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<DoctorDTO> register(@Valid @RequestBody DoctorDTO doctorDTO) {
        try {
            DoctorDTO created = authService.register(doctorDTO);
            return ResponseEntity
                    .created(URI.create("/doctors/" + created.id()))
                    .body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}