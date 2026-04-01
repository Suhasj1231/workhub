package com.smj.workhub.activity.repository;

import com.smj.workhub.activity.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository extends
        JpaRepository<ActivityLog, Long>,
        JpaSpecificationExecutor<ActivityLog> {
    Page<ActivityLog> findByWorkspaceId(Long workspaceId, Pageable pageable);

    Page<ActivityLog> findByProjectId(Long projectId, Pageable pageable);

    Page<ActivityLog> findByTaskId(Long taskId, Pageable pageable);

}