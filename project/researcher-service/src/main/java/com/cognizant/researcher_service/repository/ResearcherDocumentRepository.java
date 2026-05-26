package com.cognizant.researcher_service.repository;

import com.cognizant.researcher_service.entity.ResearcherDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResearcherDocumentRepository extends JpaRepository<ResearcherDocument, Long> {

    // Fixed: Matching the field name researcherID directly
    Optional<ResearcherDocument> findByDocumentIDAndResearcherID(Long documentID, Long researcherID);

    @Query("SELECT rd FROM ResearcherDocument rd " +
            "JOIN Researcher r ON rd.researcherID= r.researcherID " +
            "WHERE r.userid = :userId")
    List<ResearcherDocument> findByUserId(@Param("userId") Long userId);

    List<ResearcherDocument> findByVerificationStatus(String status);

}

