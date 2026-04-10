package com.smj.workhub.task.specification;

import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.entity.TaskPriority;
import com.smj.workhub.task.entity.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> filterTasks(
            Long projectId,
            TaskStatus status,
            TaskPriority priority,
            String search,
            Boolean includeDeleted,
            Boolean assignedToMe,
            Long currentUserId
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Project filter (mandatory)
            if (projectId != null) {
                predicates.add(cb.equal(root.get("project").get("id"), projectId));
            }

            // Soft delete filter
            if (includeDeleted == null || !includeDeleted) {
                predicates.add(cb.isFalse(root.get("deleted")));
            }

            // Status filter
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Priority filter
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }

            // Title search
            if (search != null && !search.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + search.toLowerCase() + "%"
                        )
                );
            }

            // Assigned to current user filter
            if (Boolean.TRUE.equals(assignedToMe) && currentUserId != null) {
                predicates.add(cb.equal(root.get("assignedTo").get("id"), currentUserId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
