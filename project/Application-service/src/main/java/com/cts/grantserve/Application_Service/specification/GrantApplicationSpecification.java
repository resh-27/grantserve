package com.cts.grantserve.Application_Service.specification;

import com.cts.grantserve.Application_Service.entity.GrantApplication;
import com.cts.grantserve.Application_Service.enums.ApplicationStatus;
import org.springframework.data.jpa.domain.Specification;

public class GrantApplicationSpecification {

    // For Search: matches title like %term%
    public static Specification<GrantApplication> hasName(String title) {
        return (root, query, cb) -> {
            if (title == null || title.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    // For Filtering: matches the Enum status
    public static Specification<GrantApplication> hasStatus(ApplicationStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<GrantApplication> hasResearcherId(Long researcherId) {
        return (root, query, cb) -> {
            if (researcherId == null) return null;
            return cb.equal(root.get("researcherId"), researcherId);
        };
    }

    public static Specification<GrantApplication> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("applicationID"), id); // Ensure this matches your Entity field name
        };
    }
}