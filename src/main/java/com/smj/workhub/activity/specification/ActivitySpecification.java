package com.smj.workhub.activity.specification;

import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.activity.entity.ActivityLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class ActivitySpecification {

    public static Specification<ActivityLog> hasWorkspaceId(Long workspaceId) {
        return (root, query, cb) ->
                workspaceId == null ? null : cb.equal(root.get("workspaceId"), workspaceId);
    }

    public static Specification<ActivityLog> hasProjectId(Long projectId) {
        return (root, query, cb) ->
                projectId == null ? null : cb.equal(root.get("projectId"), projectId);
    }

    public static Specification<ActivityLog> hasTaskId(Long taskId) {
        return (root, query, cb) ->
                taskId == null ? null : cb.equal(root.get("taskId"), taskId);
    }

    public static Specification<ActivityLog> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    public static Specification<ActivityLog> hasAction(ActivityAction action) {
        return (root, query, cb) ->
                action == null ? null : cb.equal(root.get("action"), action);
    }

    public static Specification<ActivityLog> createdAfter(Instant from) {
        return (root, query, cb) ->
                from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<ActivityLog> createdBefore(Instant to) {
        return (root, query, cb) ->
                to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}