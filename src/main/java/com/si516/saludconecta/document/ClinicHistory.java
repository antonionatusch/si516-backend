package com.si516.saludconecta.document;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy HH:mm:ss",
            timezone = "America/La_Paz")
    private Instant createdAt;
    // getters/setters, constructores
}

