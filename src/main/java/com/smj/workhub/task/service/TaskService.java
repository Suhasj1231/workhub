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

    Task getTaskById(Long id);

    Page<Task> getTasks(
            Long projectId,
            TaskStatus status,
            TaskPriority priority,
            String search,
            Boolean includeDeleted,
            Pageable pageable
    );

    Task updateTask(Long id, UpdateTaskRequest request);

    void deleteTask(Long id);

    Task restoreTask(Long id);

    Task updateTaskStatus(Long id, TaskStatus status);
}