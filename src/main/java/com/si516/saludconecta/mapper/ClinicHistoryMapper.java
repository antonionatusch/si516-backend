package com.si516.saludconecta.mapper;

import com.si516.saludconecta.document.ClinicHistory;
import com.si516.saludconecta.dto.ClinicHistoryDTO;
import com.si516.saludconecta.dto.DoctorDTO;

public class ClinicHistoryMapper {
    public static ClinicHistory toEntity(ClinicHistoryDTO dto) {
        return new ClinicHistory(
                dto.id(),
                dto.doctorId(),
                dto.officeId(),
                dto.patientId(),
                dto.visitReason(),
                dto.diagnosis(),
                dto.symptoms(),
                dto.treatment(),
                dto.prescription(),
                null // createdAt will be set by the service layer
        );
    }

    public static ClinicHistoryDTO toDTO(ClinicHistory clinicHistory) {
        return new ClinicHistoryDTO(
                clinicHistory.getId(),
                clinicHistory.getDoctorId(),
                clinicHistory.getOfficeId(),
                clinicHistory.getPatientId(),
                clinicHistory.getVisitReason(),
                clinicHistory.getDiagnosis(),
                clinicHistory.getSymptoms(),
                clinicHistory.getTreatment(),
                clinicHistory.getPrescription()
        );
    }
}
