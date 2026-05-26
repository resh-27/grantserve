package com.cts.grantserve.program_service.util;

import com.cts.grantserve.program_service.dto.*;
import com.cts.grantserve.program_service.entity.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClassUtilSeparator {

    public static Program programUtil(ProgramDto programDto) {
        Program program = new Program();

        program.setProgramID(programDto.programID());
        program.setTitle(programDto.title());
        program.setDescription(programDto.description());
        program.setBudget(programDto.budget());
        program.setStartDate(programDto.startDate());
        program.setEndDate(programDto.endDate());
        program.setStatus(programDto.status());

        return program;
    }

    public static ProgramDto convertToDto(Program program) {
        return new ProgramDto(
                program.getProgramID(),
                program.getTitle(),
                program.getDescription(),
                program.getStartDate(),
                program.getEndDate(),
                program.getBudget(),
                program.getStatus()
        );
    }


}
