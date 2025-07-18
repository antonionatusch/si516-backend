package com.si516.saludconecta.repository;

import com.si516.saludconecta.document.ClinicHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClinicHistoryRepository extends MongoRepository<ClinicHistory, String> {
}
