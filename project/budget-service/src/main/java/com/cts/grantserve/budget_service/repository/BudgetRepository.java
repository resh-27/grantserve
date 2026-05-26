package com.cts.grantserve.budget_service.repository;

import com.cts.grantserve.budget_service.entity.Budget;
import com.cts.grantserve.budget_service.projection.IBudgetProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Query("SELECT b FROM Budget b")
    List<IBudgetProjection> findAllProjectedBy();

    @Query("SELECT b FROM Budget b WHERE b.programId IN :programIds")
    List<IBudgetProjection> findAllProjectedByProgramIdIn(
            @Param("programIds") List<Long> programIds);

    Optional<IBudgetProjection> findProjectedByBudgetID(Long id);

    Optional<IBudgetProjection> findProjectedByProgramId(Long programId);

    @Modifying
    @Transactional
    @Query("UPDATE Budget b SET b.status = com.cts.grantserve.budget_service.enums.BudgetStatus.CLOSED " +
            "WHERE b.programId = :programId AND b.status = com.cts.grantserve.budget_service.enums.BudgetStatus.ACTIVE")
    int updateBudgetStatusToClosed(@Param("programId") Long programId);
}