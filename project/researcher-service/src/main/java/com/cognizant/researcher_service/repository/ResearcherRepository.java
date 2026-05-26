package com.cognizant.researcher_service.repository;

import com.cognizant.researcher_service.entity.Researcher;
import com.cognizant.researcher_service.projection.IResearcherProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResearcherRepository extends JpaRepository<Researcher, Long> {

    Optional<IResearcherProjection> findResearcherByResearcherID(Long researcherID);

    Optional<Researcher> findByUserid(Long userid);
    List<Researcher> findByInstitution(String institution);


    List<Researcher> findByStatus(String status);
}