package com.cts.grantserve.Application_Service.util;



import com.cts.grantserve.Application_Service.dto.GrantApplicationDto;
import com.cts.grantserve.Application_Service.dto.ProposalDto;
import com.cts.grantserve.Application_Service.entity.GrantApplication;
import com.cts.grantserve.Application_Service.entity.Proposal;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassUtilSeparator {


    public static GrantApplication createGrantApplication(GrantApplicationDto dto){
        GrantApplication grantApplication = new GrantApplication();
        grantApplication.setTitle(dto.title());
        grantApplication.setResearcherId(dto.researcherID());
        grantApplication.setProgramId(dto.programID());
        return  grantApplication;
    }

    public static Proposal proposalUtil(ProposalDto proposalDto){
        Proposal proposal =new Proposal();
        proposal.setFileURI(proposalDto.fileURI());
        proposal.setSubmittedDate(LocalDateTime.now());
        return  proposal;
    }

}
