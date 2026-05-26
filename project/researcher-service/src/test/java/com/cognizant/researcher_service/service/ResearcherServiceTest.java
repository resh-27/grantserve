package com.cognizant.researcher_service.service;

import com.cognizant.researcher_service.dto.RegisterresponseDto; // NEW IMPORT
import com.cognizant.researcher_service.dto.ResearcherDto;
import com.cognizant.researcher_service.entity.Researcher;
import com.cognizant.researcher_service.exception.ResearcherException;
import com.cognizant.researcher_service.projection.IResearcherProjection;
import com.cognizant.researcher_service.repository.ResearcherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResearcherServiceTest {

    @Mock
    private ResearcherRepository researcherRepository;

    @InjectMocks
    private ResearcherServiceImpl researcherService;

    private ResearcherDto researcherDto;
    private RegisterresponseDto registerResponseDto; // NEW DTO VARIABLE
    private Researcher researcherEntity;

    @BeforeEach
    void setUp() {
        // 1. DTO for profile updates (Full details)
        researcherDto = new ResearcherDto(
                "Sree",
                101L,
                "9876543210",
                LocalDate.parse("1995-09-09"),
                "Female",
                "Cognizant University",
                "IT"
        );

        // 2. DTO for initial registration (Matching your Feign client logic)
        // Ensure parameter names Name and Userid match your Record definition
        registerResponseDto = new RegisterresponseDto("Sree", 101L);

        // 3. Setup your entity mock
        researcherEntity = new Researcher();
        researcherEntity.setResearcherID(1L);
        researcherEntity.setName("Sree");
        researcherEntity.setUserid(101L);
        researcherEntity.setStatus("PENDING_PROFILE");
    }

    @Test
    @DisplayName("Registration - Should save researcher skeleton from Auth-Service DTO")
    void testRegisterNewResearcher_Success() {
        // Arrange
        when(researcherRepository.save(any(Researcher.class))).thenReturn(researcherEntity);

        // Act - CHANGED: Passing registerResponseDto instead of researcherDto
        String result = researcherService.registerNewResearcher(registerResponseDto);

        // Assert - Adjusted to match the string return in your ServiceImpl
        assertNotNull(result);
        assertTrue(result.contains("Registered Successfully") || result.contains("ID:"));
        verify(researcherRepository, times(1)).save(any(Researcher.class));
    }

    @Test
    @DisplayName("Fetch - Should throw exception when researcher ID does not exist")
    void testFetchResearcher_NotFound() {
        // Arrange
        when(researcherRepository.findResearcherByResearcherID(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResearcherException.class, () -> {
            researcherService.fetchResearcher(99L);
        });
    }

    @Test
    @DisplayName("Update - Should successfully update researcher details")
    void testUpdateResearcher_Success() throws ResearcherException {
        // Arrange - Update still uses the full ResearcherDto
        when(researcherRepository.findById(1L)).thenReturn(Optional.of(researcherEntity));
        when(researcherRepository.save(any(Researcher.class))).thenReturn(researcherEntity);

        // Act
        String result = researcherService.UpdateResearcher(1L, researcherDto);

        // Assert
        assertEquals("Researcher Updated Successfully", result);
        verify(researcherRepository, times(1)).save(any(Researcher.class));
    }
}