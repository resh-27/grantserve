package com.cts.grantserve.Application_Service.repository;



import com.cts.grantserve.Application_Service.entity.GrantApplication;
import com.cts.grantserve.Application_Service.enums.ApplicationStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGrantApplicationRepository extends JpaRepository<GrantApplication,Long>, JpaSpecificationExecutor<GrantApplication> {

    @Modifying
    @Transactional
    @Query("UPDATE GrantApplication a SET a.status = :status WHERE a.id = :id")
    int updateStatusById(@Param("status") ApplicationStatus status, @Param("id") Long id);

    Long countByResearcherId(Long id);

    @Query("SELECT DISTINCT a.programId FROM GrantApplication a WHERE a.researcherId = :userId")
    List<Long> findAllAppliedProgramIdsByResearcherId(@Param("userId") Long userId);

    Optional<List<GrantApplication>> findByProgramId(Long programID);

    Page<GrantApplication> findByResearcherId(Long id, Pageable pageable);
    Page<GrantApplication> findByResearcherIdAndStatus(Long id,Enum status,Pageable pageable);

    // In your GrantApplicationDao interface
    boolean existsByResearcherIdAndProgramId(Long researcherId, Long programId);

    Long countByResearcherIdAndStatus(Long id, ApplicationStatus applicationStatus);
}
