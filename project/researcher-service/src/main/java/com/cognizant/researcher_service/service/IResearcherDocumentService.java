package com.cognizant.researcher_service.service;

import com.cognizant.researcher_service.dto.ResearcherDocumentDto;
import com.cognizant.researcher_service.entity.ResearcherDocument;
import com.cognizant.researcher_service.exception.ResearcherDocumentException;

import java.util.List;
import java.util.Optional;

public interface IResearcherDocumentService {
    String uploadDocument(ResearcherDocumentDto documentDto) throws ResearcherDocumentException;
    Optional<ResearcherDocument> getDocument(Long id);

    List<ResearcherDocument> getDocumentByResearcherId(Long id);

    List<ResearcherDocument> getAllDocuments();

    // Updated signature
    String deleteDocument(Long researcherId, Long documentId) throws ResearcherDocumentException;
    List<ResearcherDocument> getDocumentsByStatus(String status);

    // to change the status
    String updateDocumentStatus(Long documentId, String status) throws ResearcherDocumentException;
}

