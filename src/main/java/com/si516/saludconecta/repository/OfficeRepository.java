package com.si516.saludconecta.repository;

import com.si516.saludconecta.document.Office;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfficeRepository extends MongoRepository<Office, String> {
}
