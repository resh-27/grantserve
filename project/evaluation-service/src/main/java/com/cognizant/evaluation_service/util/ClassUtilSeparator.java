package com.cognizant.evaluation_service.util;

import com.cognizant.evaluation_service.dto.EvaluationDto;
import com.cognizant.evaluation_service.entity.Evaluation;
import lombok.Data;

import java.time.LocalDate;


@Data
public class ClassUtilSeparator {

    public static Evaluation evaluationUtil(EvaluationDto evaluationDto) {
        Evaluation eval = new Evaluation();
        eval.setApplicationID(evaluationDto.applicationID());
        eval.setResult(evaluationDto.result());
        eval.setDate(LocalDate.now());
        eval.setNotes(evaluationDto.notes());
        return eval;
    }


}
