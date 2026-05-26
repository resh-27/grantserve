package com.cognizant.researcher_service.projection;

public interface IResearcherProjection {
    Long getResearcherID();
    String getName();
    String getDepartment();
    String getInstitution();
    String getStatus();
    String getContactInfo();
}
