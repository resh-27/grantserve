package com.cts.grantserve.Application_Service.controller;

import com.cts.grantserve.Application_Service.dto.GrantApplicationDto;
import com.cts.grantserve.Application_Service.dto.ProgramAnalyticsDto;
import com.cts.grantserve.Application_Service.entity.GrantApplication;
import com.cts.grantserve.Application_Service.exception.GrantApplicationException;
import com.cts.grantserve.Application_Service.service.IGrantApplicationService;
import com.cts.grantserve.Application_Service.dto.response.GrantApplicationResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // Added for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




@RestController
@RequestMapping("/GrantApplication")
@Slf4j // This provides the 'log' variable automatically
public class GrantApplicationController {

    @Autowired
    private IGrantApplicationService iGrantApplicationService;


    @PostMapping("/createApplication")
    public ResponseEntity<GrantApplicationResponse> createApplication(@Valid @RequestBody GrantApplicationDto grantApplication) {
        try {
            log.info("Received request to create a new Grant Application for data: {}", grantApplication);
            ResponseEntity<GrantApplicationResponse> response = iGrantApplicationService.createApplication(grantApplication);
            log.info("Successfully created application. Service response: {}", response);
            return response;
        } catch (GrantApplicationException e) {
            log.warn("Application creation failed - Business Logic Error: {}", e.getMessage());
            GrantApplicationResponse errorResponse = new GrantApplicationResponse();
            errorResponse.setStatus(e.getHttpStatus().value());
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(e.getHttpStatus()).body(errorResponse);
        }
    }



    @GetMapping("/FetchGrantApplication/{id}")
    public ResponseEntity<Page<GrantApplication>> FetchGrantApplication(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String search) { // Added search parameter

        log.info("Request received - User ID: {}, Status: {}, Search: {}, Page: {}",
                id, status, search, pageable.getPageNumber());

        // Pass the search term to the service
        Page<GrantApplication> applications = iGrantApplicationService.fetchGrantApplication(id, pageable, status, search);

        log.debug("Found {} applications for User ID: {}", applications.getTotalElements(), id);

        // Returning 200 OK even if empty so Angular can handle the "No Data" UI gracefully
        return ResponseEntity.ok(applications);
    }

    @DeleteMapping("/DeleteApplication/{id}")
    public ResponseEntity<String> DeleteApplication(@PathVariable long id) {
        log.warn("Request received to delete Grant Application with ID: {}", id);
        String result = iGrantApplicationService.DeleteApplication(id);
        log.info("Delete operation completed for ID: {}. Result: {}", id, result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("getApplication/{id}")
    public ResponseEntity<GrantApplication> getApplication(@PathVariable long id) {
        log.info("Retrieving specific Grant Application details for ID: {}", id);
        GrantApplication application = iGrantApplicationService.getApplication(id);
        return ResponseEntity.status(HttpStatus.OK).body(application);
    }

    @GetMapping("/UserApplicationCount/{id}")
    public ResponseEntity<Map<String, Long>> getApplicationCount(@PathVariable Long id) {
        log.info("Fetching status-wise application counts for User ID: {}", id);
        Map<String, Long> counts = iGrantApplicationService.getuserApplicationCount(id);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/AppliedProgramIds/{userId}")
    public ResponseEntity<List<Long>> getAppliedProgramIds(@PathVariable Long userId) {
        List<Long> appliedIds = iGrantApplicationService.getAppliedProgramIds(userId);

        // Even if the user has applied for nothing, return 200 OK with an empty list []
        return ResponseEntity.ok(appliedIds);
    }

    @GetMapping("/ProgramGrantApplications/{programID}")
    public ResponseEntity<List<GrantApplication>> fetchProgramGrantApplications(@PathVariable Long programID) {
        log.info("Fetching applications associated with Program ID: {}", programID);
        return iGrantApplicationService.fetchProgramGrantApplications(programID)
                .map(apps -> {
                    log.info("Found applications for Program ID: {}", programID);
                    return ResponseEntity.ok(apps);
                })
                .orElseGet(() -> {
                    log.error("No applications found for Program ID: {}", programID);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/checkGrantApplicationExists/{id}")
    public boolean grantApplicationExist(@PathVariable Long id) {
        log.debug("Checking existence of Grant Application ID: {}", id);
        boolean exists = iGrantApplicationService.grantApplicationExist(id);
        log.debug("Application ID {} exists: {}", id, exists);
        return exists;
    }

    @PutMapping("/updateStatusById")
    public boolean updateStatusById(@RequestParam String status, @RequestParam Long Id) {
        log.info("Updating status to '{}' for Application ID: {}", status, Id);
        boolean isUpdated = iGrantApplicationService.updateStatusById(status, Id);
        if (isUpdated) {
            log.info("Status successfully updated for ID: {}", Id);
        } else {
            log.error("Failed to update status for ID: {}", Id);
        }
        return isUpdated;
    }

    @GetMapping("/bulk-analytics")
    public ResponseEntity<Map<Long, ProgramAnalyticsDto>> getBulkAnalytics(@RequestParam List<Long> programIds) {
        log.info("Generating consolidated analytics for Program IDs: {}", programIds);
        Map<Long, ProgramAnalyticsDto> stats = iGrantApplicationService.getBulkProgramAnalytics(programIds);
        return ResponseEntity.ok(stats);
    }

    // Add this inside your GrantApplicationController class
    @GetMapping("/all")
    public ResponseEntity<List<GrantApplication>> getAllApplications() {
        log.info("Manager request to fetch all grant applications");
        // You'll need to ensure your service has a method to find all records
        List<GrantApplication> allApplications = iGrantApplicationService.findAllApplications();
        return ResponseEntity.ok(allApplications);
    }

}