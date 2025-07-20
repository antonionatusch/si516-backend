package com.si516.saludconecta.document;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("clinic_histories")
public class ClinicHistory {
    @Id
    private String id;
    private String doctorId;
    private String officeId;
    private String patientId;
    private String visitReason;
    private String diagnosis;
    private List<String> symptoms;
    private List<Treatment> treatment;      // clase embebida
    private Pickup pickup;      // clase embebida
    private Instant createdAt;
    // getters/setters, constructores
}

