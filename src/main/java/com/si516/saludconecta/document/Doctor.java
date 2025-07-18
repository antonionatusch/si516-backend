package com.si516.saludconecta.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("doctors")
public class Doctor {
    @Id
    private String id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String officeId;    // referencia manual a Office.id
    // getters/setters, constructores
}

