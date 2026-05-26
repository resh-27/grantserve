package com.cts.grantserve.Application_Service.service;



import com.cts.grantserve.Application_Service.dto.GrantApplicationDto;
import com.cts.grantserve.Application_Service.dto.ProgramAnalyticsDto;
import com.cts.grantserve.Application_Service.entity.GrantApplication;
import com.cts.grantserve.Application_Service.exception.GrantApplicationException;
import com.cts.grantserve.Application_Service.dto.response.GrantApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IGrantApplicationService {

    ResponseEntity<GrantApplicationResponse> createApplication(GrantApplicationDto grantApplication);

     String DeleteApplication(Long id);

     public GrantApplication getApplication(Long id) throws GrantApplicationException;
    List<Long> getAppliedProgramIds(Long userId);


    public Page<GrantApplication> fetchGrantApplication(Long id, Pageable pageable, String status, String searchTerm) throws GrantApplicationException;

    Optional<List<GrantApplication>> fetchProgramGrantApplications(Long programID) throws GrantApplicationException;

    public Map<String, Long> getuserApplicationCount(Long id);

    boolean grantApplicationExist(Long id);

    boolean updateStatusById(String status, Long id);

    Map<Long, ProgramAnalyticsDto> getBulkProgramAnalytics(List<Long> programIds);
    List<GrantApplication> findAllApplications();

}
