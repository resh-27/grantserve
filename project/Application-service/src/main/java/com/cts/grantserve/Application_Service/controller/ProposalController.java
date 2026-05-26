package com.cts.grantserve.Application_Service.controller;

import com.cts.grantserve.Application_Service.dto.ProposalDto;
import com.cts.grantserve.Application_Service.dto.response.ProposalResponse;
import com.cts.grantserve.Application_Service.entity.Proposal;
import com.cts.grantserve.Application_Service.projection.IProposalProjection;
import com.cts.grantserve.Application_Service.service.IProposalService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // Added for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/proposal")
@Slf4j // Injects the 'log' object
public class ProposalController {

    @Autowired
    private IProposalService proposalService;

    @PostMapping("/createProposal")
    public ResponseEntity<ProposalResponse> createProposal(@Valid @RequestBody ProposalDto proposalDto) {
        log.info("Request received to create a new proposal for Application ID: {}", proposalDto.applicationID());
        ProposalResponse response = proposalService.createProposal(proposalDto).getBody();
        log.info("Proposal created successfully. Service response: {}", response);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getProposal/{applicationId}")
    public ResponseEntity<List<IProposalProjection>> getProposal(@PathVariable Long applicationId) {
        log.info("Fetching proposal projections for application ID: {}", applicationId);
        List<IProposalProjection> projections = proposalService.getProposal(applicationId);
        log.debug("Successfully retrieved {} projection records for ID: {}", projections.size(), applicationId);
        return ResponseEntity.status(HttpStatus.OK).body(projections);
    }

    @GetMapping("/checkProposalExists/{id}")
    public boolean grantApplicationExist(@PathVariable Long id) {
        log.debug("Checking if proposal with ID {} exists", id);
        boolean exists = proposalService.checkIfProposalExist(id);
        log.info("Proposal existence check for ID {}: {}", id, exists);
        return exists;
    }

    @PutMapping("/updateStatusById")
    public boolean updateStatusById(@RequestParam String status, @RequestParam Long Id) {
        log.info("Request to update status of Proposal ID: {} to '{}'", Id, status);
        boolean isUpdated = proposalService.updateStatusById(status, Id);
        if (isUpdated) {
            log.info("Status successfully updated for Proposal ID: {}", Id);
        } else {
            log.error("Failed to update status for Proposal ID: {}. Check if ID exists.", Id);
        }
        return isUpdated;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proposal> getProposalById(@PathVariable Long id) {
        Proposal proposal = proposalService.getProposalById(id);
        return ResponseEntity.ok(proposal);
    }

    @GetMapping("/application/{appId}")
    public ResponseEntity<List<Long>> getIds(@PathVariable Long appId) {
        return ResponseEntity.ok(proposalService.getProposalIdsByApplication(appId));
    }
}