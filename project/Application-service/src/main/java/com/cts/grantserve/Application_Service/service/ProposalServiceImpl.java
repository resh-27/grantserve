package com.cts.grantserve.Application_Service.service;

import com.cts.grantserve.Application_Service.dto.ProposalDto;
import com.cts.grantserve.Application_Service.dto.response.ProposalResponse;
import com.cts.grantserve.Application_Service.entity.GrantApplication;
import com.cts.grantserve.Application_Service.entity.Proposal;
import com.cts.grantserve.Application_Service.exception.ProposalException;
import com.cts.grantserve.Application_Service.projection.IProposalProjection;
import com.cts.grantserve.Application_Service.repository.IGrantApplicationRepository;
import com.cts.grantserve.Application_Service.repository.IProposalRepository;
import com.cts.grantserve.Application_Service.util.ClassUtilSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProposalServiceImpl implements IProposalService {

    @Autowired
    private IProposalRepository proposalDao;

    @Autowired
    private IGrantApplicationRepository grantApplicationRepository;

    public boolean checkIfProposalExist(Long id) {
        log.debug("Checking existence for Proposal ID: {}", id);
        return proposalDao.existsById(id);
    }

    @Override
    public boolean updateStatusById(String status, Long id) {
        log.info("Attempting to update status to '{}' for Proposal ID: {}", status, id);
        int rowsAffected = proposalDao.updateStatusById(status, id);

        if (rowsAffected == 0) {
            log.error("Update failed: No proposal found with ID: {}", id);
            throw new RuntimeException("No application found with ID: " + id);
        }

        log.info("Status successfully updated for Proposal ID: {}. Rows affected: {}", id, rowsAffected);
        return rowsAffected > 0;
    }

    public ResponseEntity<ProposalResponse> createProposal(ProposalDto proposalDto) throws ProposalException {
        log.info("Initiating proposal creation for Application ID: {}", proposalDto.applicationID());

        // Map DTO to Entity
        Proposal proposal = ClassUtilSeparator.proposalUtil(proposalDto);
        ProposalResponse response = new ProposalResponse();
        // Fetch the associated Grant Application
        GrantApplication application = grantApplicationRepository.findById(proposalDto.applicationID())
                .orElseThrow(() -> {
                    log.error("Proposal creation failed: Grant Application ID {} not found", proposalDto.applicationID());

                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Application Not found");
                    return new ProposalException("Application Not found", HttpStatus.NOT_FOUND);
                });

        proposal.setGrantApplication(application);
        proposal.setStatus("SUBMITTED");

        Proposal savedProposal = proposalDao.save(proposal);
        log.info("Proposal successfully saved. Generated Proposal ID: {} linked to Application ID: {}",
                savedProposal.getProposalID(), proposalDto.applicationID());
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("Proposal submitted successfully with ID: " + savedProposal.getProposalID());


        return ResponseEntity.ok(response);
    }

    public List<IProposalProjection> getProposal(Long id) {
        log.info("Fetching projected data for applicationId ID: {}", id);
        List<IProposalProjection> results = proposalDao.findProjectedById(id);

        if (results.isEmpty()) {
            log.warn("No projections found for applicationId ID: {}", id);
        } else {
            log.debug("Retrieved {} projection records for applicationId ID: {}", results.size(), id);
        }

        return results;
    }

        public List<Long> getProposalIdsByApplication(Long appId) {
            return proposalDao.findByApplicationID(appId)
                    .stream()
                    .map(Proposal::getProposalID)
                    .collect(Collectors.toList());
        }

    public Proposal getProposalById(Long id) {
        return proposalDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + id));
    }

}