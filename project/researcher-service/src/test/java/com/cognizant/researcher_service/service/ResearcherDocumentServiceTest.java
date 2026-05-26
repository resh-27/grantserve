package com.cognizant.researcher_service.service;

import com.cognizant.researcher_service.entity.Researcher;
import com.cognizant.researcher_service.entity.ResearcherDocument;
import com.cognizant.researcher_service.exception.ResearcherDocumentException;
import com.cognizant.researcher_service.repository.ResearcherDocumentRepository;
import com.cognizant.researcher_service.repository.ResearcherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResearcherDocumentServiceTest {

    @Mock
    private ResearcherDocumentRepository documentRepository;

    @Mock
    private ResearcherRepository researcherRepository;

    @InjectMocks
    private ResearcherDocumentServiceImpl documentService;

    private ResearcherDocument sampleDoc;

    @BeforeEach
    void setUp() {
        sampleDoc = new ResearcherDocument();
        sampleDoc.setDocumentID(1L);
        sampleDoc.setResearcherID(101L);
        sampleDoc.setVerificationStatus("Pending");
    }

    @Test
    @DisplayName("Should successfully update document status to Approved")
    void testUpdateDocumentStatus_Success() throws ResearcherDocumentException {
        // Arrange
        when(documentRepository.findById(1L)).thenReturn(Optional.of(sampleDoc));
        when(documentRepository.save(any(ResearcherDocument.class))).thenReturn(sampleDoc);

        // Act
        String result = documentService.updateDocumentStatus(1L, "Approved");

        // Assert
        assertEquals("Document status updated to Approved", result);
        assertEquals("Approved", sampleDoc.getVerificationStatus());
        verify(documentRepository, times(1)).save(sampleDoc);
    }

    @Test
    @DisplayName("Should throw exception when document is not found")
    void testUpdateDocumentStatus_NotFound() {
        // Arrange
        when(documentRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResearcherDocumentException.class, () -> {
            documentService.updateDocumentStatus(2L, "Approved");
        });

        verify(documentRepository, never()).save(any());
    }
}