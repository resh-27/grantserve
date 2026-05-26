package com.cognizant.researcher_service.service;

import com.cognizant.researcher_service.dto.RegisterresponseDto;
import com.cognizant.researcher_service.dto.ResearcherDto;
import com.cognizant.researcher_service.entity.Researcher;
import com.cognizant.researcher_service.exception.ResearcherException;
import com.cognizant.researcher_service.projection.IResearcherProjection;

import java.util.List;
import java.util.Optional;

public interface IResearcherService {
    String UpdateResearcher(Long id,ResearcherDto researcherDto) throws ResearcherException;
    IResearcherProjection fetchResearcher(Long id) throws ResearcherException;


    Researcher getResearcherByUserId(Long id);
    String registerNewResearcher(RegisterresponseDto registerresponsedto);
    List<Researcher> getResearchersByInstitution(String institution);
    List<Researcher> getResearchersByStatus(String status);
}
