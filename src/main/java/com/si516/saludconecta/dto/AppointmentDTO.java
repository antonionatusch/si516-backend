package com.si516.saludconecta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {

    private String appointmentId;
    private String officeId;
    private String doctorId;
    private String patientId;
    private LocalDateTime appointmentDate;
    // private String status; // Comentado porque también está comentado en el documento
}