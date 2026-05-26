package com.cts.grantserve.program_service.controller;

import com.cts.grantserve.program_service.dto.BudgetUpdateRequest;
import com.cts.grantserve.program_service.dto.ProgramDto;
import com.cts.grantserve.program_service.enums.ProgramStatus;
import com.cts.grantserve.program_service.projection.IProgramProjection;
import com.cts.grantserve.program_service.service.IProgramService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/programs")
public class ProgramController {

    @Autowired
    private IProgramService programService;

    @PostMapping("/createProgram")
    public ResponseEntity<ProgramDto> createProgram(@Valid @RequestBody ProgramDto programDto) {
        return programService.createProgram(programDto);
    }

    @GetMapping
    public ResponseEntity<List<IProgramProjection>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProgram(@Valid @RequestBody ProgramDto programDto) {
        return programService.updateProgram(programDto);
    }

    @GetMapping("/active")
    public List<IProgramProjection> getActiveApplications(){
        return programService.getActiveApplications(LocalDate.now());
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<String> publishProgram(
            @PathVariable Long id,
            @Valid @RequestBody BudgetUpdateRequest request) {

        programService.publishProgram(id, request.budget());
        return ResponseEntity.ok("Program published successfully with budget: " + request.budget());
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return programService.existsById(id);
    }

    @GetMapping("/manager/search")
    public PagedModel<IProgramProjection> searchProgramApplicationsForManager(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) ProgramStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PageableDefault(size = 10, sort = "programID", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PagedModel<>(programService.searchProgramsForManager(title, id, status, start, end, pageable));
    }

    @GetMapping("/search")
    public PagedModel<IProgramProjection> searchProgramApplications(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) ProgramStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PageableDefault(size = 10, sort = "programID", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new PagedModel<>(programService.searchProgramsForPublic(title, id, status, start, end, pageable));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<String> closeProgram(@PathVariable Long id) {
        return programService.updateProgramStatusToClosed(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraftProgram(@PathVariable Long id) {
        return programService.deleteDraftProgram(id);
    }

}