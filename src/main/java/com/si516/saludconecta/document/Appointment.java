package com.si516.saludconecta.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("appointments")
public class Appointment {

    @Id
    private String appointmentId;
    private Office office;
    private Doctor doctor;
    private Patient patient;

    private LocalDateTime appointmentDate;
    //private String status; // SCHEDULED, COMPLETED, CANCELLED
}
