package com.smj.workhub.activity.service.impl;

import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.activity.entity.ActivityLog;
import com.smj.workhub.activity.repository.ActivityRepository;
import com.smj.workhub.activity.service.ActivityService;
import com.smj.workhub.activity.specification.ActivitySpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // 🔥 WRITE
    @Override
    public void logActivity(
            Long userId,
            ActivityAction action,
            Long workspaceId,
            Long projectId,
            Long taskId,
            String description,
            String metadata
    ) {
        ActivityLog activity = new ActivityLog();

        activity.setUserId(userId);
        activity.setAction(action);
        activity.setWorkspaceId(workspaceId);
        activity.setProjectId(projectId);
        activity.setTaskId(taskId);
        activity.setDescription(description);
        activity.setMetadata(metadata);

        activityRepository.save(activity);

        log.info("Activity logged: userId={}, action={}, workspaceId={}, projectId={}, taskId={}",
                userId, action, workspaceId, projectId, taskId);
    }

    // 🔍 READ
    @Override
    public Page<ActivityLog> getActivities(
            Long workspaceId,
            Long projectId,
            Long taskId,
            Long userId,
            ActivityAction action,
            Instant from,
            Instant to,
            Pageable pageable
    ) {

        Specification<ActivityLog> spec = Specification
                .where(ActivitySpecification.hasWorkspaceId(workspaceId))
                .and(ActivitySpecification.hasProjectId(projectId))
                .and(ActivitySpecification.hasTaskId(taskId))
                .and(ActivitySpecification.hasUserId(userId))
                .and(ActivitySpecification.hasAction(action))
                .and(ActivitySpecification.createdAfter(from))
                .and(ActivitySpecification.createdBefore(to));

        return activityRepository.findAll(spec, pageable);
    }
}