package com.smj.workhub.activity.service;

import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.activity.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface ActivityService {

    // 🔥 CORE: reusable logging method
    void logActivity(
            Long userId,
            ActivityAction action,
            Long workspaceId,
            Long projectId,
            Long taskId,
            String description,
            String metadata
    );

    // 🔍 Fetch activities (with filtering)
    Page<ActivityLog> getActivities(
            Long workspaceId,
            Long projectId,
            Long taskId,
            Long userId,
            ActivityAction action,
            Instant from,
            Instant to,
            Pageable pageable
    );

    Page<ActivityLog> getWorkspaceActivities(Long workspaceId, Pageable pageable);

    Page<ActivityLog> getProjectActivities(Long projectId, Pageable pageable);

    Page<ActivityLog> getTaskActivities(Long taskId, Pageable pageable);
}