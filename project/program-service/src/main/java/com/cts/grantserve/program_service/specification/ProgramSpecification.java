package com.cts.grantserve.program_service.specification;

import com.cts.grantserve.program_service.entity.Program;
import com.cts.grantserve.program_service.enums.ProgramStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class ProgramSpecification {

    public static Specification<Program> hasName(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isEmpty()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Program> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("id"), id);
        };
    }

    public static Specification<Program> hasNotStatus(ProgramStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.notEqual(root.get("status"), status);
        };
    }

    public static Specification<Program> orderBy(String field, String direction) {
        return (root, query, cb) -> {
            if ("desc".equalsIgnoreCase(direction)) {
                query.orderBy(cb.desc(root.get(field)));
            } else {
                query.orderBy(cb.asc(root.get(field)));
            }
            return null; // Return null because we only want the side-effect (sorting)
        };
    }

    public static Specification<Program> hasStatus(ProgramStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;

            Predicate statusPredicate = root.get("status").in(status);

            if (status.equals(ProgramStatus.ACTIVE)) {
                Predicate predicate = cb.lessThanOrEqualTo(root.get("startDate"), LocalDate.now());
                return cb.and(statusPredicate, predicate);
            }
            if (status.equals(ProgramStatus.FORECASTED)) {
                return cb.and(
                        cb.equal(root.get("status"), ProgramStatus.ACTIVE),
                        cb.greaterThan(root.get("startDate"), LocalDate.now())
                );
            }
            return statusPredicate;
        };
    }

    public static Specification<Program> startsAfter(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.greaterThanOrEqualTo(root.get("startDate"), date);
        };
    }

    public static Specification<Program> endsBefore(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) return null;
            return cb.lessThanOrEqualTo(root.get("endDate"), date);
        };
    }

    public static Specification<Program> isWithinRange(LocalDate rangeStart, LocalDate rangeEnd) {
        return (root, query, cb) -> {
            if (rangeStart == null || rangeEnd == null) return null;

            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("endDate"), rangeStart),
                    cb.lessThanOrEqualTo(root.get("startDate"), rangeEnd)
            );
        };
    }

}
