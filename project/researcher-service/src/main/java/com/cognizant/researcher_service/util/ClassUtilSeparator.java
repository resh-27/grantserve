package com.cognizant.researcher_service.util;

import com.cognizant.researcher_service.dto.*;
import com.cognizant.researcher_service.entity.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClassUtilSeparator {

    public static Researcher researcherRegisterUtil(ResearcherDto dto, Researcher existingResearcher) {
        existingResearcher.setName(dto.name());
        //existingResearcher.setUserid(dto.userid());
        existingResearcher.setContactInfo(dto.contactInfo());
        existingResearcher.setDob(dto.dob());
        existingResearcher.setGender(dto.gender());
        existingResearcher.setInstitution(dto.institution());
        existingResearcher.setDepartment(dto.department());

        // ADD THIS LINE TO FLIP THE STATUS
        existingResearcher.setStatus("ACTIVE");

        return existingResearcher;
    }

    public static ResearcherDocument documentUploadUtil(ResearcherDocumentDto dto) {
        ResearcherDocument doc = new ResearcherDocument();
        doc.setDocType(dto.docType());
        doc.setFileURI(dto.fileURI());

        // Link the researcher using the ID from the DTO
        Researcher researcher = new Researcher();
        researcher.setResearcherID(dto.researcherID());
        doc.setResearcherID(researcher.getResearcherID());

        return doc;
    }


}
