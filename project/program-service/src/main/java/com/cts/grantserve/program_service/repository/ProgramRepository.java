package com.cts.grantserve.program_service.repository;

import com.cts.grantserve.program_service.entity.Program;
import com.cts.grantserve.program_service.projection.IProgramProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long>, JpaSpecificationExecutor<Program> {

    @Query("SELECT p FROM Program p")
    List<IProgramProjection> findAllProjectedBy();

    Optional<IProgramProjection> findProjectedByProgramID(Long id);

    default List<IProgramProjection> findAllProjectedBy(Specification<Program> spec) {
        return findBy(spec, q -> q.as(IProgramProjection.class).all());
    }

    @Modifying
    @Transactional
    @Query("UPDATE Program p SET p.status = com.cts.grantserve.program_service.enums.ProgramStatus.CLOSED WHERE p.programID = :programId AND p.status = com.cts.grantserve.program_service.enums.ProgramStatus.ACTIVE")
    int updateProgramStatusToClosed(@Param("programId") Long programId);

    @Query("SELECT p FROM Program p WHERE p.startDate <= :now AND p.endDate >= :now AND p.status = com.cts.grantserve.program_service.enums.ProgramStatus.ACTIVE")
    List<IProgramProjection> findActiveApplications(@Param("now") LocalDate now);

    @Modifying
    @Transactional
    @Query("UPDATE Program p SET p.budgetId = :budgetId WHERE p.programID = :id")
    int updateBudgetIdById(@Param("id") Long id, @Param("budgetId") Long budgetId);

    default Page<IProgramProjection> findAllProjectedBy(Specification<Program> spec, Pageable pageable) {
        return findBy(spec, q -> q.as(IProgramProjection.class).page(pageable));
    }

}