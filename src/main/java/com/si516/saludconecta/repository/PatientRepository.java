package com.si516.saludconecta.repository;

import com.si516.saludconecta.document.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByName(String name);
}
