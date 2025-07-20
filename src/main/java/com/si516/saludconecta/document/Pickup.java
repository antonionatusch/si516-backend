package com.si516.saludconecta.document;

import com.si516.saludconecta.enums.PickupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pickup {
    private PickupType pickupType;   // NOW, SCHEDULED o LATER
    private String scheduledTime;    // formato "HH:mm" o null si no aplica
}

