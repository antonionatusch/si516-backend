package com.si516.saludconecta.repository;

import com.si516.saludconecta.document.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
}
