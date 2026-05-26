package com.cognizant.evaluation_service.service;



import com.cognizant.evaluation_service.dto.EvaluationDto;
import com.cognizant.evaluation_service.entity.Evaluation;
import com.cognizant.evaluation_service.enums.Result;
import com.cognizant.evaluation_service.exception.EvaluationNotFoundException;
import com.cognizant.evaluation_service.feignclients.GrantApplicationFeignClient;
import com.cognizant.evaluation_service.repository.EvaluationRepository;
import com.cognizant.evaluation_service.util.ClassUtilSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class EvaluationServiceImpl implements IEvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private GrantApplicationFeignClient grantApplicationFeignClient;



    @Override
    @Transactional
    public String createEvaluation(EvaluationDto evaluationDto) {
        log.info("Service: Creating Evaluation for Application ID: {}", evaluationDto.applicationID());
        Boolean exists = grantApplicationFeignClient.grantApplicationExist(evaluationDto.applicationID());

        if (exists == null || !exists) {
            log.error("Service Error: Application ID {} does not exist in APPLICATION-SERVICE", evaluationDto.applicationID());
            throw new RuntimeException("Application not found");
        }
        String statusResult = String.valueOf(evaluationDto.result());
        boolean isUpdated = grantApplicationFeignClient.updateStatusById(statusResult, evaluationDto.applicationID());

        if (!isUpdated) {
            log.error("Failed to update status for Application ID: {}", evaluationDto.applicationID());
            throw new RuntimeException("Remote status update failed");
        }
        Evaluation eval = ClassUtilSeparator.evaluationUtil(evaluationDto);
        evaluationRepository.save(eval);
        return "Evaluation processed. Grant Application status updated to " + statusResult;
    }

    @Override
    public List<Evaluation> getAllEvaluations() {
        log.info("Service: Fetching all evaluation records");
        return evaluationRepository.findAll();
    }

    @Override
    public Evaluation getEvaluationById(long id) {
        log.info("Service: Searching for evaluation ID: {}", id);
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new EvaluationNotFoundException("Evaluation ID " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public String deleteEvaluation(long id) {
        log.info("Service: Deleting evaluation ID: {}", id);
        Evaluation eval = getEvaluationById(id);
        evaluationRepository.delete(eval);
        return "Evaluation record removed successfully";
    }
    @Override
    public LocalDate getApprovedDate(Long applicationID) throws EvaluationNotFoundException {
        log.info("Fetching approved date for Application ID: {}", applicationID);
        Evaluation evaluation = evaluationRepository.findByApplicationIDAndResult(applicationID, Result.APPROVED)
                .orElseThrow(() -> new EvaluationNotFoundException("No approved evaluation found for Application ID: " + applicationID, HttpStatus.NOT_FOUND));
        return evaluation.getDate();
    }

}