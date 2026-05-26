package com.cognizant.researcher_service.controller;

import com. cognizant.researcher_service.dto.ResearcherDocumentDto;
import com.cognizant.researcher_service.entity.ResearcherDocument;
import com.cognizant.researcher_service.exception.ResearcherDocumentException;
import com.cognizant.researcher_service.service.IResearcherDocumentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/documents")
public class ResearcherDocumentController {

    @Autowired
    private IResearcherDocumentService researcherDocumentService;

    @PostMapping("/upload")
    public String upload(@Valid @RequestBody ResearcherDocumentDto documentDto) throws ResearcherDocumentException {
        log.info("REST request to upload document for Researcher ID: {}", documentDto.researcherID());
        String response = researcherDocumentService.uploadDocument(documentDto);
        log.info("Document upload status: {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public Optional<ResearcherDocument> getDocument(@PathVariable Long id) {
        log.info("REST request to fetch document details for ID: {}", id);
        return researcherDocumentService.getDocument(id);
    }

    @DeleteMapping("/delete/{researcherId}/{documentId}")
    public String delete(
            @PathVariable Long researcherId,
            @PathVariable Long documentId) throws ResearcherDocumentException {

        log.warn("REST request to DELETE Document ID: {} belonging to Researcher ID: {}", documentId, researcherId);
        String response = researcherDocumentService.deleteDocument(researcherId, documentId);
        log.info("Successfully processed deletion for Document ID: {}", documentId);
        return response;
    }

    @GetMapping("/researcher/{id}")
    public List<ResearcherDocument> getDocumentByResearcherId(@PathVariable Long id) {
        log.info("REST request to fetch document details for researcher ID: {}", id);
        return researcherDocumentService.getDocumentByResearcherId(id);
    }

    //to update the status
    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) throws ResearcherDocumentException {
        log.info("REST request to update status of Document ID: {} to {}", id, status);
        return researcherDocumentService.updateDocumentStatus(id, status);
    }

    @GetMapping("/pending")
    public List<ResearcherDocument> getPendingDocuments() {
        log.info("Compliance Officer fetching all documents with 'Pending' status");
        // This calls your already existing service method
        return researcherDocumentService.getDocumentsByStatus("Pending");
    }
}

