package com.cts.grantserve.program_service.service;

import com.cts.grantserve.program_service.dto.ProgramDto;
import com.cts.grantserve.program_service.enums.ProgramStatus;
import com.cts.grantserve.program_service.projection.IProgramProjection;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IProgramService {

    @Transactional
    ResponseEntity<ProgramDto> createProgram(ProgramDto programDto);

    @Transactional
    ResponseEntity<String> updateProgram(ProgramDto programDto);

    @Transactional
    ResponseEntity<String> updateProgramStatusToClosed(Long id);

    Optional<IProgramProjection> getProgram(Long id);

    List<IProgramProjection> getAllPrograms();

    List<IProgramProjection> getActiveApplications(LocalDate now);

    Page<IProgramProjection> searchProgramsForPublic(String title, Long id, ProgramStatus status, LocalDate start, LocalDate end, Pageable pageable);

    Page<IProgramProjection> searchProgramsForManager(String title, Long id, ProgramStatus status, LocalDate start, LocalDate end, Pageable pageable);

    ResponseEntity<Boolean> existsById(Long id);

    void publishProgram(Long id, @NotNull(message = "Budget amount is required") @Min(value = 1, message = "Budget must be at least 1") Double budget);

    ResponseEntity<Void> deleteDraftProgram(Long id);
}