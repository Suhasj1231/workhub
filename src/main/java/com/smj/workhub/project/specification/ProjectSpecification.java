package com.smj.workhub.project.specification;

import com.smj.workhub.project.entity.Project;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class ProjectSpecification {

    private ProjectSpecification() {
        // Utility class
    }

    public static Specification<Project> build(
            Long workspaceId,
            String search,
            Boolean includeDeleted
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // -------- Workspace filter (mandatory) --------
            predicates.add(
                    cb.equal(
                            root.get("workspace").get("id"),
                            workspaceId
                    )
            );

            // -------- Deleted filter --------
            if (includeDeleted == null || !includeDeleted) {
                predicates.add(
                        cb.isFalse(root.get("deleted"))
                );
            }

            // -------- Name search (case insensitive) --------
            if (search != null && !search.trim().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + search.toLowerCase().trim() + "%"
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}