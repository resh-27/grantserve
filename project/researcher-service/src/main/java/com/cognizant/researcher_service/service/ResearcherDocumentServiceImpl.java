package com.cognizant.researcher_service.service;

import com.cognizant.researcher_service.dto.ResearcherDocumentDto;
import com.cognizant.researcher_service.entity.Researcher;
import com.cognizant.researcher_service.entity.ResearcherDocument;
import com.cognizant.researcher_service.exception.ResearcherDocumentException;
import com.cognizant.researcher_service.exception.ResearcherException;
import com.cognizant.researcher_service.repository.ResearcherDocumentRepository;
import com.cognizant.researcher_service.repository.ResearcherRepository;
import com.cognizant.researcher_service.util.ClassUtilSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ResearcherDocumentServiceImpl implements IResearcherDocumentService {

    @Autowired
    private ResearcherDocumentRepository researcherDocumentRepository;

    @Autowired
    private ResearcherRepository researcherRepository;

    @Override
    public String uploadDocument(ResearcherDocumentDto documentDto) throws ResearcherDocumentException {
        log.info("Initiating document upload for Researcher ID: {}", documentDto.researcherID());

        // 1. Convert DTO to Entity
        ResearcherDocument doc = ClassUtilSeparator.documentUploadUtil(documentDto);

        // 2. Look up the Researcher
        Researcher researcher = researcherRepository.findById(documentDto.researcherID())
                .orElseThrow(() -> {
                    log.error("Upload failed: Researcher ID {} not found", documentDto.researcherID());
                    return new ResearcherException(
                            "Cannot upload document. Researcher not found with ID: " + documentDto.researcherID(),
                            HttpStatus.NOT_FOUND);
                });

        // 3. FIX: Changed setResearcher_id to setResearcherID to match entity field
        doc.setResearcherID(researcher.getResearcherID());
        doc.setUploadedDate(LocalDateTime.now());
        doc.setVerificationStatus("Pending");

        // 4. Save
        researcherDocumentRepository.save(doc);

        log.info("Document successfully uploaded and linked to Researcher ID: {}", researcher.getResearcherID());
        return "Document Uploaded Successfully";
    }

    @Override
    public Optional<ResearcherDocument> getDocument(Long id) {
        log.debug("Fetching document details for Document ID: {}", id);
        return researcherDocumentRepository.findById(id);
    }

    @Override
    public List<ResearcherDocument> getDocumentByResearcherId(Long id) {
        log.debug("Fetching document details for Researcher ID: {}", id);
        return researcherDocumentRepository.findByUserId(id);
    }

    @Override
    public List<ResearcherDocument> getAllDocuments() {
        log.info("Retrieving all documents from the system");
        return researcherDocumentRepository.findAll();
    }

    @Override
    public String deleteDocument(Long researcherId, Long documentId) throws ResearcherDocumentException {
        log.info("Attempting to delete Document ID: {} for Researcher ID: {}", documentId, researcherId);

        // 5. FIX: Corrected the repository call and method name
        ResearcherDocument doc = researcherDocumentRepository
                .findByDocumentIDAndResearcherID(documentId, researcherId)
                .orElseThrow(() -> {
                    log.warn("Delete failed: Document ID {} not found for Researcher ID {}", documentId, researcherId);
                    return new ResearcherDocumentException(
                            "Document not found or does not belong to researcher ID: " + researcherId,
                            HttpStatus.NOT_FOUND);
                });

        researcherDocumentRepository.delete(doc);
        log.info("Document ID: {} successfully deleted", documentId);
        return "Document Deleted Successfully";
    }

    @Override
    public List<ResearcherDocument> getDocumentsByStatus(String status) {
        log.info("Filtering documents by status: {}", status);
        return researcherDocumentRepository.findByVerificationStatus(status); // Now used!
    }

    // Add this to ResearcherDocumentServiceImpl.java
    @Override
    public String updateDocumentStatus(Long documentId, String status) throws ResearcherDocumentException {
        log.info("Compliance Officer updating status for Document ID: {} to {}", documentId, status);

        ResearcherDocument doc = researcherDocumentRepository.findById(documentId)
                .orElseThrow(() -> {
                    log.error("Update failed: Document ID {} not found", documentId);
                    return new ResearcherDocumentException("Document not found with ID: " + documentId, HttpStatus.NOT_FOUND);
                });

        doc.setVerificationStatus(status);
        researcherDocumentRepository.save(doc);

        log.info("Document ID: {} status successfully updated to {}", documentId, status);
        return "Document status updated to " + status;
    }
}
