package com.smj.workhub.activity.repository;

import com.smj.workhub.activity.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository extends
        JpaRepository<ActivityLog, Long>,
        JpaSpecificationExecutor<ActivityLog> {
}