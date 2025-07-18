package com.si516.saludconecta.repository;

import com.si516.saludconecta.document.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
}
