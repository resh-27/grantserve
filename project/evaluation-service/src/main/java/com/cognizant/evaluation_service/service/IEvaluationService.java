package com.cognizant.evaluation_service.service;



import com.cognizant.evaluation_service.dto.EvaluationDto;
import com.cognizant.evaluation_service.entity.Evaluation;
import com.cognizant.evaluation_service.exception.EvaluationNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface IEvaluationService {
    String createEvaluation(EvaluationDto evaluationDto);
    List<Evaluation> getAllEvaluations();
    Evaluation getEvaluationById(long id);
    String deleteEvaluation(long id);
    public LocalDate getApprovedDate(Long applicationID) throws EvaluationNotFoundException;

}