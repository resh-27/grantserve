package com.cognizant.evaluation_service.repository;

import com.cognizant.evaluation_service.entity.Evaluation;
import com.cognizant.evaluation_service.enums.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findByApplicationIDAndResult(Long applicationID, Result result);
}


