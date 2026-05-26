package com.cts.grantserve.Application_Service.service;

import com.cts.grantserve.Application_Service.enums.ApplicationStatus;
import com.cts.grantserve.Application_Service.dto.GrantApplicationDto;
import com.cts.grantserve.Application_Service.dto.ProgramAnalyticsDto;
import com.cts.grantserve.Application_Service.entity.GrantApplication;
import com.cts.grantserve.Application_Service.exception.GrantApplicationException;
import com.cts.grantserve.Application_Service.feign.IEvaluationFeign;
import com.cts.grantserve.Application_Service.feign.IProgramFeignIntreface;
import com.cts.grantserve.Application_Service.repository.IGrantApplicationRepository;
import com.cts.grantserve.Application_Service.specification.GrantApplicationSpecification;
import com.cts.grantserve.Application_Service.util.ClassUtilSeparator;
import com.cts.grantserve.Application_Service.dto.response.GrantApplicationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GrantApplicationServiceImpl implements IGrantApplicationService {

    @Autowired
    IGrantApplicationRepository grantApplicationDao;

    @Autowired
    IProgramFeignIntreface programFeignIntreface;

    @Autowired
    private IEvaluationFeign evaluationFeign;




    public ResponseEntity<GrantApplicationResponse> createApplication(GrantApplicationDto grantApplicationDto) throws GrantApplicationException {
        log.info("Attempting to create application for Program ID: {}", grantApplicationDto.programID());

        GrantApplicationResponse response = new GrantApplicationResponse();

        // 1. Logging Feign Client call
        Boolean programExists = programFeignIntreface.existsById(grantApplicationDto.programID()).getBody();

        if (!Boolean.TRUE.equals(programExists)) {
            log.error("Validation Failed: Program ID {} does not exist", grantApplicationDto.programID());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("The Program ID " + grantApplicationDto.programID() + " does not exist.");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. UNIQUE CHECK: One application per program per researcher
        // grantApplicationDto should provide the researcherId
        boolean alreadyApplied = grantApplicationDao.existsByResearcherIdAndProgramId(
                grantApplicationDto.researcherID(),
                grantApplicationDto.programID()
        );

        if (alreadyApplied) {
            log.warn("Duplicate Application: Researcher {} already applied to Program {}",
                    grantApplicationDto.researcherID(), grantApplicationDto.programID());


            response.setStatus(HttpStatus.CONFLICT.value());
            response.setMessage("You have already submitted an application for this program.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // 3. Proceed with creation
        GrantApplication grantApplication = ClassUtilSeparator.createGrantApplication(grantApplicationDto);
        grantApplication.setStatus(ApplicationStatus.valueOf("SUBMITTED"));
        grantApplication.setSubmittedDate(LocalDate.now());

        GrantApplication savedApplication = grantApplicationDao.save(grantApplication);
        log.info("Grant Application saved successfully for Researcher ID: {}", grantApplication.getResearcherId());

        // Populate response with created application data
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Application created successfully");
        response.setApplicationID(savedApplication.getApplicationID());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    public ResponseEntity<String> displayFallback(GrantApplicationDto grantApplication, Throwable t) {
        log.error("Circuit Breaker 'ApplicationServe' triggered. Reason: {}", t.getMessage());
        String errorMessage = "ID cannot be verified now. Please try again later.";
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorMessage);
    }


    public String DeleteApplication(Long id) throws GrantApplicationException {
        log.info("Processing deletion for Application ID: {}", id);
        if (!grantApplicationDao.existsById(id)) {
            log.warn("Delete failed: Application ID {} not found in database", id);
            throw new GrantApplicationException("Delete failed: No application found with ID " + id, HttpStatus.NOT_FOUND);
        }
        grantApplicationDao.deleteById(id);
        log.info("Application ID {} deleted successfully", id);
        return "Deleted SuccessFully";
    }

    public GrantApplication getApplication(Long id) throws GrantApplicationException {
        log.info("Fetching application details for ID: {}", id);
        return grantApplicationDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Application retrieval failed: ID {} not found", id);
                    return new GrantApplicationException("Application not found for ID: " + id, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public List<Long> getAppliedProgramIds(Long userId) {
        log.info("Fetching applied program IDs for user: {}", userId);
        return grantApplicationDao.findAllAppliedProgramIdsByResearcherId(userId);
    }

    @Override
    public Page<GrantApplication> fetchGrantApplication(Long id, Pageable pageable, String status, String searchTerm) throws GrantApplicationException {
        log.info("Fetching applications for User: {} | Status: {} | Search: {} | Page: {}",
                id, status, searchTerm, pageable.getPageNumber());

        // 1. Start with the mandatory Researcher ID specification
        Specification<GrantApplication> spec = Specification.allOf(GrantApplicationSpecification.hasResearcherId(id));

        // 2. Add Status Filter (only if not "ALL")
        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            try {
                String formattedStatus = status.toUpperCase().replace(" ", "_");
                ApplicationStatus statusEnum = ApplicationStatus.valueOf(formattedStatus);
                spec = spec.and(GrantApplicationSpecification.hasStatus(statusEnum));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter received: {}. Skipping status filter.", status);
                // We don't return Page.empty() yet, we just ignore the invalid status
            }
        }

        // 3. Add Search Term (using the 'hasName' method from your spec class)
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            spec = spec.and(GrantApplicationSpecification.hasName(searchTerm));
        }

        // 4. Use the Specification Executor to find everything in ONE go
        // Note: Ensure your repository extends JpaSpecificationExecutor
        Page<GrantApplication> applicationPage = grantApplicationDao.findAll(spec, pageable);

        if (applicationPage.isEmpty()) {
            log.info("No applications found matching the criteria for user {}", id);
            // We return the empty page object (200 OK) so the frontend can show "No Data Found"
        }

        return applicationPage;
    }
    @Override
    public Map<String, Long> getuserApplicationCount(Long id) {
        Map<String, Long> counts = new HashMap<>();

        // 1. Total Count (All)
        counts.put("All", grantApplicationDao.countByResearcherId(id));

        // 2. Count by specific Enums
        counts.put("Submitted", grantApplicationDao.countByResearcherIdAndStatus(id, ApplicationStatus.SUBMITTED));
        counts.put("Under Review", grantApplicationDao.countByResearcherIdAndStatus(id, ApplicationStatus.UNDER_REVIEW));
        counts.put("Approved", grantApplicationDao.countByResearcherIdAndStatus(id, ApplicationStatus.APPROVED));
        counts.put("Rejected", grantApplicationDao.countByResearcherIdAndStatus(id, ApplicationStatus.REJECTED));

        return counts;
    }

    @Override
    public Optional<List<GrantApplication>> fetchProgramGrantApplications(Long programID) throws GrantApplicationException {
        log.info("Retrieving applications for Program ID: {}", programID);
        return grantApplicationDao.findByProgramId(programID);
    }

    public boolean grantApplicationExist(Long id) {
        return grantApplicationDao.existsById(id);
    }

    @Override
    public boolean updateStatusById(String status, Long id) {
        log.info("Updating status to '{}' for Application ID: {}", status, id);

        // 1. Convert the String to the Enum type
        ApplicationStatus statusEnum;
        try {
            statusEnum = ApplicationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid status value provided: {}", status);
            throw new RuntimeException("Invalid status: " + status);
        }

        // 2. Pass the Enum (not the String) to the DAO
        int rowsAffected = grantApplicationDao.updateStatusById(statusEnum, id);

        if (rowsAffected == 0) {
            log.error("Update failed: No application records matched ID: {}", id);
            throw new RuntimeException("No application found with ID: " + id);
        }

        log.info("Status update successful for ID: {}. Rows affected: {}", id, rowsAffected);
        return rowsAffected > 0;
    }

    @Override
    public Map<Long, ProgramAnalyticsDto> getBulkProgramAnalytics(List<Long> programIds) {
        Map<Long, ProgramAnalyticsDto> responseMap = new HashMap<>();

        for (Long pId : programIds) {
            // Fetch applications for the specific program
            List<GrantApplication> apps = grantApplicationDao.findByProgramId(pId).orElse(List.of());

            long total = apps.size();
            // Correctly filter using the enum constant
            long approved = apps.stream()
                    .filter(a -> ApplicationStatus.APPROVED.equals(a.getStatus()))
                    .count();

            double acceptanceRate = total == 0 ? 0.0 : ((double) approved / total) * 100;

            // Grouping by YearMonth ensures chronological sorting (Jan -> Feb -> Mar)
            Map<java.time.YearMonth, Map<ApplicationStatus, Long>> groupedByMonth = apps.stream()
                    .collect(Collectors.groupingBy(
                            app -> java.time.YearMonth.from(app.getSubmittedDate()),
                            TreeMap::new,
                            Collectors.groupingBy(
                                    GrantApplication::getStatus,
                                    Collectors.counting()
                            )
                    ));

            List<String> labels = new ArrayList<>();
            List<Long> acceptedCounts = new ArrayList<>();
            List<Long> rejectedCounts = new ArrayList<>();
            List<Long> pendingCounts = new ArrayList<>();

            for (var entry : groupedByMonth.entrySet()) {
                labels.add(entry.getKey().getMonth().name());
                Map<ApplicationStatus, Long> statusCounts = entry.getValue();

                acceptedCounts.add(statusCounts.getOrDefault(ApplicationStatus.APPROVED, 0L));
                rejectedCounts.add(statusCounts.getOrDefault(ApplicationStatus.REJECTED, 0L));

                // Aggregating SUBMITTED and UNDER_REVIEW as 'pending'
                pendingCounts.add(
                        statusCounts.getOrDefault(ApplicationStatus.UNDER_REVIEW, 0L) +
                        statusCounts.getOrDefault(ApplicationStatus.SUBMITTED, 0L)
                );
            }

            Map<String, Object> monthlyStats = new HashMap<>();
            monthlyStats.put("labels", labels);
            monthlyStats.put("accepted", acceptedCounts);
            monthlyStats.put("rejected", rejectedCounts);
            monthlyStats.put("pending", pendingCounts);

            responseMap.put(pId, new ProgramAnalyticsDto(total, approved, acceptanceRate, monthlyStats));
        }
        return responseMap;
    }

    @Override
    public List<GrantApplication> findAllApplications() {
        return grantApplicationDao.findAll();
    }

}