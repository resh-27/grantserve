package com.cts.grantserve.program_service.service;

import com.cts.grantserve.program_service.dto.ProgramDto;
import com.cts.grantserve.program_service.entity.Program;
import com.cts.grantserve.program_service.enums.ProgramStatus;
import com.cts.grantserve.program_service.exception.ProgramNotFoundException;
import com.cts.grantserve.program_service.exception.ProgramNotModifiableException;
import com.cts.grantserve.program_service.projection.IProgramProjection;
import com.cts.grantserve.program_service.repository.ProgramRepository;
import com.cts.grantserve.program_service.specification.ProgramSpecification;
import com.cts.grantserve.program_service.util.ClassUtilSeparator;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements IProgramService {

    private final ProgramRepository programRepository;
    private final BudgetServiceHandler budgetHandler;

    @Transactional
    @Override
    public ResponseEntity<ProgramDto> createProgram(ProgramDto programDto) {
        Program program = ClassUtilSeparator.programUtil(programDto);
        Program savedProgram = programRepository.save(program);

        if (savedProgram.getStatus() == ProgramStatus.ACTIVE) {
            log.info("Program {} is ACTIVE. Initializing budget record.", savedProgram.getProgramID());

            Long budgetId = budgetHandler.initializeBudget(savedProgram);
            programRepository.updateBudgetIdById(savedProgram.getProgramID(), budgetId);
        }

        return new ResponseEntity<>(ClassUtilSeparator.convertToDto(savedProgram), HttpStatus.CREATED);
    }

    @Transactional
    @Override
    public ResponseEntity<String> updateProgram(ProgramDto programDto) {
        Long id = programDto.programID();

        Program existingProgram = programRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException("Program not found with id: " + id));

        if (existingProgram.getStatus() != ProgramStatus.DRAFT) {
            throw new ProgramNotModifiableException("Update failed. Only DRAFT programs can be modified.");
        }

        Program updatedProgram = ClassUtilSeparator.programUtil(programDto);
        programRepository.save(updatedProgram);

        if (programDto.status() == ProgramStatus.ACTIVE) {
            log.info("Program {} updated to ACTIVE. Checking for budget initialization.", id);
            try {
                budgetHandler.getBudgetByProgramId(id);
            } catch (FeignException.NotFound ex) {
                Long budgetId = budgetHandler.initializeBudget(updatedProgram);
                programRepository.updateBudgetIdById(id, budgetId);
            }
        }

        String message = "Program updated successfully with status: " + programDto.status() +
                (programDto.status() == ProgramStatus.ACTIVE ? " and budget activated." : ".");

        return ResponseEntity.ok(message);
    }

    @Transactional
    public void publishProgram(Long id, Double budgetAmount) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException("Program not found with id: " + id));

        if (program.getStatus() != ProgramStatus.DRAFT) {
            throw new ProgramNotModifiableException("Update failed. Only DRAFT programs can be modified.");
        }

        // Update fields
        program.setBudget(budgetAmount);
        program.setStatus(ProgramStatus.ACTIVE);

        log.info("Program {} updated to ACTIVE. Checking for budget initialization.", id);
        try {
            budgetHandler.getBudgetByProgramId(id);
        } catch (FeignException.NotFound ex) {
            Long budgetId = budgetHandler.initializeBudget(id, budgetAmount);
            programRepository.updateBudgetIdById(id, budgetId);
        }

        programRepository.save(program);
    }

    @Override
    public ResponseEntity<Void> deleteDraftProgram(Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException("Program not found with id: " + id));

        if (!program.getStatus().equals(ProgramStatus.DRAFT)) {
            throw new IllegalStateException("Only draft programs can be deleted.");
        }
        programRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    public ResponseEntity<String> updateProgramStatusToClosed(Long id) {
        int rowsAffected = programRepository.updateProgramStatusToClosed(id);

        if (rowsAffected == 0) {
            throw new ProgramNotModifiableException("Cannot close program. Invalid ID or already CLOSED.");
        }

        log.info("Program ID: {} marked as CLOSED. Closing associated budget.", id);
        String res = budgetHandler.closeBudget(id);

        String message = "Program status updated to CLOSED successfully. " + res;
        return ResponseEntity.ok(message);
    }

    @Override
    public Optional<IProgramProjection> getProgram(Long id) {
        return programRepository.findProjectedByProgramID(id);
    }

    @Override
    public List<IProgramProjection> getAllPrograms() {
        return programRepository.findAllProjectedBy();
    }

    @Cacheable(value = "activeGrants", key = "#now")
    public List<IProgramProjection> getActiveApplications(LocalDate now) {
        return programRepository.findActiveApplications(now);
    }

    @Override
    public Page<IProgramProjection> searchProgramsForPublic(String title, Long id, ProgramStatus status, LocalDate start, LocalDate end, Pageable pageable) {
        Specification<Program> spec = Specification.allOf();
        spec = spec.and(ProgramSpecification.hasNotStatus(ProgramStatus.DRAFT));
        return applyFiltersAndSort(spec, title, id, status, start, end, pageable);
    }

    @Override
    public Page<IProgramProjection> searchProgramsForManager(String title, Long id, ProgramStatus status, LocalDate start, LocalDate end, Pageable pageable) {
        Specification<Program> spec = Specification.allOf();
        return applyFiltersAndSort(spec, title, id, status, start, end, pageable);
    }

    private Page<IProgramProjection> applyFiltersAndSort(Specification<Program> spec, String title, Long id, ProgramStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (title != null && !title.isEmpty()) {
            spec = spec.and(ProgramSpecification.hasName(title));
        }
        if (id != null) {
            spec = spec.and(ProgramSpecification.hasId(id));
        }
        if (status != null) {
            spec = spec.and(ProgramSpecification.hasStatus(status));
        }
        if (startDate != null &&  endDate != null) {
            spec = spec.and(ProgramSpecification.isWithinRange(startDate, endDate));
        } else if (startDate != null) {
            spec = spec.and(ProgramSpecification.startsAfter(startDate));
        } else if (endDate != null) {
            spec = spec.and(ProgramSpecification.endsBefore(endDate));
        }

        return programRepository.findAllProjectedBy(spec, pageable);
    }

    @Override
    public ResponseEntity<Boolean> existsById(Long id) {
        return new ResponseEntity<>(programRepository.existsById(id), HttpStatus.OK);
    }

}