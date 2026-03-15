package com.smj.workhub.task.service.impl;

import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.project.entity.Project;
import com.smj.workhub.project.repository.ProjectRepository;
import com.smj.workhub.task.dto.CreateTaskRequest;
import com.smj.workhub.task.dto.UpdateTaskRequest;
import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.entity.TaskPriority;
import com.smj.workhub.task.entity.TaskStatus;
import com.smj.workhub.task.repository.TaskRepository;
import com.smj.workhub.task.service.TaskService;
import com.smj.workhub.task.specification.TaskSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            ProjectRepository projectRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    // CREATE TASK
    @Override
    public Task createTask(Long projectId, CreateTaskRequest request) {
        log.info("Creating task title={} for projectId={}", request.title(), projectId);

        Project project = projectRepository.findByIdAndDeletedFalse(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        )
                );

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.title());
        task.setDescription(request.description());

        task.setStatus(
                request.status() != null ? request.status() : TaskStatus.TODO
        );

        task.setPriority(
                request.priority() != null ? request.priority() : TaskPriority.MEDIUM
        );

        task.setDueDate(request.dueDate());

        Task saved = taskRepository.save(task);
        log.info("Task created successfully id={} projectId={}", saved.getId(), projectId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Task getProjectPreview(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found with id: " + projectId)
                );

        Task preview = new Task();
        preview.setProject(project);

        return preview;
    }

    // GET TASK BY ID
    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        log.debug("Fetching task id={}", id);
        return taskRepository.findByIdWithProjectAndWorkspace(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );
    }

    // LIST TASKS WITH FILTERS
    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasks(
            Long projectId,
            TaskStatus status,
            TaskPriority priority,
            String search,
            Boolean includeDeleted,
            Pageable pageable
    ) {
        log.debug("Fetching tasks for projectId={} status={} priority={} search={} includeDeleted={}",
                projectId, status, priority, search, includeDeleted);

        Specification<Task> spec = TaskSpecification.filterTasks(
                projectId,
                status,
                priority,
                search,
                includeDeleted
        );

        return taskRepository.findAll(spec, pageable);
    }

    // UPDATE TASK
    @Override
    public Task updateTask(Long id, UpdateTaskRequest request) {
        log.info("Updating task id={}", id);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());

        Task updated = taskRepository.save(task);
        log.info("Task updated successfully id={}", updated.getId());
        return updated;
    }

    // PATCH TASK STATUS
    @Override
    public Task updateTaskStatus(Long id, TaskStatus status) {
        log.info("Updating task status id={} newStatus={}", id, status);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );

        task.setStatus(status);

        Task updated = taskRepository.save(task);
        log.info("Task status updated successfully id={} status={}", updated.getId(), updated.getStatus());
        return updated;
    }

    // SOFT DELETE
    @Override
    public void deleteTask(Long id) {
        log.warn("Soft deleting task id={}", id);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );

        task.setDeleted(true);
    }

    // RESTORE TASK
    @Override
    public Task restoreTask(Long id) {
        log.info("Restoring task id={}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );

        task.setDeleted(false);

        Task restored = taskRepository.save(task);
        log.info("Task restored successfully id={}", restored.getId());
        return restored;
    }

}