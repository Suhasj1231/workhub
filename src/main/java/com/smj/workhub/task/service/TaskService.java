package com.smj.workhub.task.service;

import com.smj.workhub.task.dto.CreateTaskRequest;
import com.smj.workhub.task.dto.UpdateTaskRequest;
import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.entity.TaskPriority;
import com.smj.workhub.task.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    Task createTask(Long projectId, CreateTaskRequest request);

    /**
     * Fetches a lightweight preview of a task via project to determine workspace access.
     * Used by controllers to verify workspace membership before performing task operations.
     */
    Task getProjectPreview(Long projectId);

    Task getTaskById(Long id);

    Page<Task> getTasks(
            Long projectId,
            TaskStatus status,
            TaskPriority priority,
            String search,
            Boolean includeDeleted,
            Boolean assignedToMe,
            Pageable pageable
    );

    Task updateTask(Long id, UpdateTaskRequest request);

    void deleteTask(Long id);

    Task restoreTask(Long id);

    Task updateTaskStatus(Long id, TaskStatus status);

    Task assignTask(Long taskId, Long userId);

    Task getTaskByIdIncludingDeleted(Long taskId);
}