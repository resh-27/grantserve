package com.cts.grantserve.Application_Service.service;


import com.cts.grantserve.Application_Service.dto.ProposalDto;
import com.cts.grantserve.Application_Service.dto.response.ProposalResponse;
import com.cts.grantserve.Application_Service.entity.Proposal;
import com.cts.grantserve.Application_Service.exception.ProposalException;
import com.cts.grantserve.Application_Service.projection.IProposalProjection;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IProposalService {
    public ResponseEntity<ProposalResponse> createProposal(ProposalDto proposal) throws ProposalException;
    public boolean checkIfProposalExist(Long id);
    List<IProposalProjection> getProposal(Long id);


    boolean updateStatusById(String status, Long id);
    public List<Long> getProposalIdsByApplication(Long appId);
    public Proposal getProposalById(Long id);
}

