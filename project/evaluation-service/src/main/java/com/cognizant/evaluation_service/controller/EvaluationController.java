package com.cognizant.evaluation_service.controller;


import com.cognizant.evaluation_service.dto.EvaluationDto;
import com.cognizant.evaluation_service.entity.Evaluation;
import com.cognizant.evaluation_service.service.IEvaluationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("evaluation") // Added /api prefix - common industry standard
public class EvaluationController {

    @Autowired
    private IEvaluationService evaluationService;

    // POST: Create a new evaluation and update Grant Application status
    @PostMapping("/submit")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> submit(@Valid @RequestBody EvaluationDto evaluationDto) {
        log.info("Controller: Received request to submit evaluation for Application ID: {}", evaluationDto.applicationID());

        String response = evaluationService.createEvaluation(evaluationDto);

        log.info("Controller: Evaluation processed successfully for Application ID: {}", evaluationDto.applicationID());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET: Retrieve all evaluation records
    @GetMapping("/all")
    //@PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'REVIEWER')")
    public ResponseEntity<List<Evaluation>> getAll() {
        log.info("Controller: Fetching all evaluations");
        List<Evaluation> evaluations = evaluationService.getAllEvaluations();

        if (evaluations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(evaluations, HttpStatus.OK);
    }

    // GET: Retrieve a single evaluation by ID (Added this missing method)
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER','REVIEWER')")
    public ResponseEntity<Evaluation> getById(@PathVariable long id) {
        log.info("Controller: Fetching evaluation with ID: {}", id);
        Evaluation evaluation = evaluationService.getEvaluationById(id);
        return new ResponseEntity<>(evaluation, HttpStatus.OK);
    }

    // DELETE: Remove an evaluation record
    @DeleteMapping("/delete/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable long id) {
        log.warn("Controller: Request to delete evaluation record ID: {}", id);

        String response = evaluationService.deleteEvaluation(id);

        log.info("Controller: Evaluation record ID: {} deleted successfully", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/approved-date/{applicationID}")
    public ResponseEntity<LocalDate> getApprovedDate(@PathVariable Long applicationID) {
        log.info("Controller: Fetching approved date for Application ID: {}", applicationID);
        LocalDate date = evaluationService.getApprovedDate(applicationID);
        return ResponseEntity.ok(date);
    }

}