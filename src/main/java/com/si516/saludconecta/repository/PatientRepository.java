package com.si516.saludconecta.repository;

import com.si516.saludconecta.document.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<Patient, String> {
}
