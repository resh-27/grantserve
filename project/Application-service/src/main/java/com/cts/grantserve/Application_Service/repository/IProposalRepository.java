package com.cts.grantserve.Application_Service.repository;


import com.cts.grantserve.Application_Service.entity.Proposal;
import com.cts.grantserve.Application_Service.projection.IProposalProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IProposalRepository extends JpaRepository<Proposal, Long> {

    @Query("SELECT p.proposalID as proposalID, " +
            "p.fileURI as fileURI, " +
            "p.submittedDate as submittedDate, " +
            "p.status as status, " +
            "p.grantApplication.applicationID as applicationID " +
            "FROM Proposal p " +
            "WHERE p.grantApplication.applicationID = :id")
    List<IProposalProjection> findProjectedById(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query("UPDATE Proposal p SET p.status = :status WHERE p.proposalID = :id")
    int updateStatusById(@Param("status") String status, @Param("id") Long id);

    @Query("SELECT p FROM Proposal p WHERE p.grantApplication.applicationID = :appId")
    List<Proposal> findByApplicationID(@Param("appId") Long appId);

}