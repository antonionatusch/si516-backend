package com.si516.saludconecta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorRepository extends MongoRepository<DoctorRepository, String> {
}
