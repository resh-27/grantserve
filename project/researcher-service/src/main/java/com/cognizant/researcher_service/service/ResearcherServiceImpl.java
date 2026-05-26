package com.cognizant.researcher_service.service;

import com.cognizant.researcher_service.dto.RegisterresponseDto;
import com.cognizant.researcher_service.dto.ResearcherDto;
import com.cognizant.researcher_service.entity.Researcher;
import com.cognizant.researcher_service.exception.ResearcherException;
import com.cognizant.researcher_service.projection.IResearcherProjection;
import com.cognizant.researcher_service.repository.ResearcherRepository;
import com.cognizant.researcher_service.util.ClassUtilSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ResearcherServiceImpl implements IResearcherService {

    @Autowired
    private ResearcherRepository researcherDAO;

    @Override
    public String registerNewResearcher(RegisterresponseDto dto) { // Fix: Use the correct DTO
        log.info("Auto-registering researcher from Auth for User ID: {}", dto.UserId());

        Researcher researcher = new Researcher();
        researcher.setName(dto.Name()); // Use the record accessor
        researcher.setUserid(dto.UserId());

        // Set default values for fields not provided by Auth-Service yet
        researcher.setStatus("PENDING_PROFILE");
        researcher.setInstitution("To be updated");
        researcher.setDepartment("To be updated");
        researcher.setGender("To be updated");
        researcher.setDob(null); // Default placeholder
        researcher.setContactInfo("Pending");

        researcherDAO.save(researcher);
        return "Researcher skeleton created for User ID: " + dto.UserId();
    }
    @Override
    public String UpdateResearcher(Long id, ResearcherDto researcherDto) throws ResearcherException {
        log.info("Attempting to update researcher for ID: {}", id);

        // Try finding by Primary Key (researcherid) first, then by linked userid
        Researcher existingResearcher = researcherDAO.findById(id)
                .orElseGet(() -> researcherDAO.findByUserid(id).orElse(null));

        if (existingResearcher == null) {
            log.error("Update failed: No researcher record found for ID: {}", id);
            throw new ResearcherException("Researcher not found for ID: " + id, HttpStatus.NOT_FOUND);
        }

        // Map fields from DTO
        ClassUtilSeparator.researcherRegisterUtil(researcherDto, existingResearcher);
        existingResearcher.setStatus("ACTIVE");

        researcherDAO.save(existingResearcher);
        log.info("Successfully updated researcher with ID: {}", existingResearcher.getResearcherID());
        return "Researcher Updated Successfully";
    }
    @Override
    public IResearcherProjection fetchResearcher(Long id) throws ResearcherException {
        log.info("Fetching researcher projection for ID: {}", id);

        return researcherDAO.findResearcherByResearcherID(id)
                .orElseThrow(() -> {
                    log.warn("Fetch failed: No projection found for ID: {}", id);
                    return new ResearcherException("Researcher not found with ID: " + id, HttpStatus.NOT_FOUND);
                });
    }



    @Override
    public Researcher getResearcherByUserId(Long id) {
        log.debug("Internal call to getResearcher Optional for ID: {}", id);
        return researcherDAO.findByUserid(id)
                .orElseThrow(() -> {
                    log.warn("Fetch failed: No projection found for ID: {}", id);
                    return new ResearcherException("Researcher not found with ID: " + id, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public List<Researcher> getResearchersByInstitution(String institution) {
        log.info("Fetching researchers for institution: {}", institution);
        return researcherDAO.findByInstitution(institution); // Now has 1 usage!
    }

    @Override
    public List<Researcher> getResearchersByStatus(String status) {
        log.info("Fetching researchers with status: {}", status);
        return researcherDAO.findByStatus(status); // Now has 1 usage!
    }
}