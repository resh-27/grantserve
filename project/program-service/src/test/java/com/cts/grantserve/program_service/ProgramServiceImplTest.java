package com.cts.grantserve.program_service;

import com.cts.grantserve.program_service.client.BudgetClient;
import com.cts.grantserve.program_service.dto.BudgetDto;
import com.cts.grantserve.program_service.dto.ProgramDto;
import com.cts.grantserve.program_service.entity.Program;
import com.cts.grantserve.program_service.enums.ProgramStatus;
import com.cts.grantserve.program_service.exception.ProgramNotFoundException;
import com.cts.grantserve.program_service.exception.ProgramNotModifiableException;
import com.cts.grantserve.program_service.repository.ProgramRepository;
import com.cts.grantserve.program_service.service.ProgramServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramServiceImplTest {

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private BudgetClient budgetClient;

    @InjectMocks
    private ProgramServiceImpl programService;

    @Test
    @DisplayName("Create Program - ACTIVE status initializes budget")
    void createProgram_activeStatus_initializesBudget() {
        ProgramDto dto = new ProgramDto(
                1L, "Title", "Description",
                LocalDate.now(), LocalDate.now().plusDays(10),
                5000.0, ProgramStatus.ACTIVE
        );

        Program savedProgram = new Program();
        savedProgram.setProgramID(1L);
        savedProgram.setStatus(ProgramStatus.ACTIVE);
        savedProgram.setBudget(5000.0);

        when(programRepository.save(any(Program.class))).thenReturn(savedProgram);
        when(budgetClient.createBudget(any(BudgetDto.class))).thenReturn(100L);

        programService.createProgram(dto);

        verify(budgetClient, times(1)).createBudget(any(BudgetDto.class));
        verify(programRepository, times(1))
                .updateBudgetIdById(eq(1L), eq(100L));
    }

    @Test
    @DisplayName("Update Program - Non DRAFT program throws exception")
    void updateProgram_nonDraft_throwsException() {
        ProgramDto dto = new ProgramDto(
                1L, "Title", "Desc",
                LocalDate.now(), LocalDate.now().plusDays(10),
                5000.0, ProgramStatus.ACTIVE
        );

        Program existing = new Program();
        existing.setStatus(ProgramStatus.ACTIVE);

        when(programRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(
                ProgramNotModifiableException.class,
                () -> programService.updateProgram(dto)
        );
    }

    @Test
    @DisplayName("Update Program - Program ID not found")
    void updateProgram_notFound_throwsException() {
        ProgramDto dto = new ProgramDto(
                1L,
                "Title",
                "Desc",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                0.0,
                ProgramStatus.DRAFT
        );

        when(programRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ProgramNotFoundException.class,
                () -> programService.updateProgram(dto)
        );
    }

    @Test
    @DisplayName("Update Program - DRAFT to ACTIVE initializes budget when missing")
    void updateProgram_draftToActive_initializesBudget() {
        ProgramDto dto = new ProgramDto(
                1L, "Title", "Desc",
                LocalDate.now(), LocalDate.now().plusDays(10),
                5000.0, ProgramStatus.ACTIVE
        );

        Program existing = new Program();
        existing.setProgramID(1L);
        existing.setStatus(ProgramStatus.DRAFT);
        existing.setBudget(5000.0);

        when(programRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(programRepository.save(any(Program.class))).thenReturn(existing);

        // Simulate budget not found
        when(budgetClient.getBudgetByProgramId(1L))
                .thenThrow(mock(FeignException.NotFound.class));
        when(budgetClient.createBudget(any(BudgetDto.class))).thenReturn(101L);

        ResponseEntity<String> response = programService.updateProgram(dto);

        assert response.getBody() != null;
        assertTrue(response.getBody().contains("Program updated successfully"));
        verify(budgetClient).createBudget(any(BudgetDto.class));
    }

    @Test
    @DisplayName("Close Program - Success")
    void updateProgramStatusToClosed_success() {
        Long programId = 1L;

        when(programRepository.updateProgramStatusToClosed(programId))
                .thenReturn(1);
        when(budgetClient.closeBudgetByProgramId(programId))
                .thenReturn("Budget CLOSED successfully.");

        ResponseEntity<String> result = programService.updateProgramStatusToClosed(programId);

        assert result.getBody() != null;
        assertTrue(result.getBody().contains("Program status updated to CLOSED successfully"));
        verify(budgetClient).closeBudgetByProgramId(programId);
    }

    @Test
    @DisplayName("Close Program - Invalid ID throws exception")
    void updateProgramStatusToClosed_invalidId_throwsException() {
        when(programRepository.updateProgramStatusToClosed(1L))
                .thenReturn(0);

        assertThrows(
                ProgramNotModifiableException.class,
                () -> programService.updateProgramStatusToClosed(1L)
        );
    }
}