package com.si516.saludconecta.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {
    private String drug;         // p.ej. "Losartan 50 mg"
    private String lab;          // p.ej. "Genfar"
    private String instruction;  // p.ej. "1 tableta cada mañana"
}

