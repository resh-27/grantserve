package com.cognizant.researcher_service.controller;

import com.cognizant.researcher_service.dto.RegisterresponseDto;
import com.cognizant.researcher_service.dto.ResearcherDto;
import com.cognizant.researcher_service.entity.Researcher;
import com.cognizant.researcher_service.exception.ResearcherException;
import com.cognizant.researcher_service.projection.IResearcherProjection;
import com.cognizant.researcher_service.repository.ResearcherRepository;
import com.cognizant.researcher_service.service.IResearcherService;
import com.cognizant.researcher_service.service.ResearcherServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/researcher")
public class ResearcherController {

    @Autowired
    private ResearcherRepository researcherRepository;

    @Autowired
    private IResearcherService researcherService;

    // 1. Update/Register a Researcher
    @PutMapping("/Update/{id}")
    public String updateResearcher(
            @PathVariable Long id,
            @Valid @RequestBody ResearcherDto researcherDto
    ) throws ResearcherException {
        log.info("REST request to update Researcher ID: {}", id);
        String response = researcherService.UpdateResearcher(id, researcherDto);
        log.info("Update successful for ID: {}", id);
        return response;
    }

    // 2. Get a Researcher by ID
    @GetMapping("/{id}")
    public IResearcherProjection getResearcher(@PathVariable Long id) throws ResearcherException {
        log.info("REST request to fetch Researcher details for ID: {}", id);
        IResearcherProjection projection = researcherService.fetchResearcher(id);
        log.debug("Successfully retrieved projection for ID: {}", id);
        return projection;
    }

    // 3. Get a Researcher by user ID
    @GetMapping("/user/{id}")
    public Researcher getResearcherByUserId(@PathVariable Long id) throws ResearcherException {
        log.info("REST request to fetch Researcher details for user ID: {}", id);
        Researcher projection = researcherService.getResearcherByUserId(id);
        log.debug("Successfully retrieved projection for user ID: {}", id);
        return projection;
    }

    // Add this to ResearcherController.java
    @PostMapping("/register")
    public String registerResearcher(@Valid @RequestBody RegisterresponseDto
                                                 registerresponsedto) {
        log.info("REST request to register a new researcher");

        // CALL THE SERVICE
        String response = researcherService.registerNewResearcher(registerresponsedto);

        return response;
    }



}