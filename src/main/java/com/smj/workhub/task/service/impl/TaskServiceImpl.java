package com.smj.workhub.task.service.impl;

import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.project.entity.Project;
import com.smj.workhub.project.repository.ProjectRepository;
import com.smj.workhub.security.principal.UserPrincipal;
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

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import com.smj.workhub.activity.service.ActivityService;
import com.smj.workhub.activity.entity.ActivityAction;
import org.springframework.security.core.context.SecurityContextHolder;

import com.smj.workhub.notification.service.NotificationService;
import com.smj.workhub.notification.entity.NotificationType;
import java.util.Objects;

import com.smj.workhub.user.repository.UserRepository;
import com.smj.workhub.workspace.repository.WorkspaceMemberRepository;
import com.smj.workhub.common.exception.AccessDeniedException;
import com.smj.workhub.workspace.entity.WorkspaceRole;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ActivityService activityService;
    private final NotificationService notificationService;

    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            ActivityService activityService,
            NotificationService notificationService,
            UserRepository userRepository,
            WorkspaceMemberRepository workspaceMemberRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.activityService = activityService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
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

        Long currentUserId = getCurrentUserId();
        task.setCreatedBy(currentUserId);

        Task saved = taskRepository.save(task);

        Long userId = currentUserId;
        String metadata = String.format("{\"status\":\"%s\",\"priority\":\"%s\",\"dueDate\":\"%s\"}",
                saved.getStatus(), saved.getPriority(), saved.getDueDate());

        activityService.logActivity(
                userId,
                ActivityAction.TASK_CREATED,
                saved.getProject().getWorkspace().getId(),
                saved.getProject().getId(),
                saved.getId(),
                "Task '" + saved.getTitle() + "' created with status " + saved.getStatus(),
                metadata
        );

        // 🔔 Notification (optional: notify workspace owner or skip for now)

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

    // GET TASK BY ID (INCLUDING DELETED)
    @Transactional(readOnly = true)
    public Task getTaskByIdIncludingDeleted(Long id) {
        log.debug("Fetching task (including deleted) id={}", id);

        return taskRepository.findByIdWithProjectAndWorkspaceIncludingDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );
    }

    // LIST TASKS WITH FILTERS
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tasks", key = "#projectId + '-' + #status + '-' + #priority + '-' + #search + '-' + #includeDeleted + '-' + #assignedToMe + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Task> getTasks(
            Long projectId,
            TaskStatus status,
            TaskPriority priority,
            String search,
            Boolean includeDeleted,
            Boolean assignedToMe,
            Pageable pageable
    ) {
        log.debug("Fetching tasks for projectId={} status={} priority={} search={} includeDeleted={} assignedToMe={}",
                projectId, status, priority, search, includeDeleted, assignedToMe);

        Long currentUserId = getCurrentUserId();

        Specification<Task> spec = TaskSpecification.filterTasks(
                projectId,
                status,
                priority,
                search,
                includeDeleted,
                assignedToMe,
                currentUserId
        );

        return taskRepository.findAll(spec, pageable);
    }

    // UPDATE TASK
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long id, UpdateTaskRequest request) {
        log.info("Updating task id={}", id);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );

        String oldTitle = task.getTitle();
        String oldDescription = task.getDescription();
        TaskStatus oldStatus = task.getStatus();
        TaskPriority oldPriority = task.getPriority();

        if (request.title() != null) {
            task.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            task.setDescription(request.description());
        }

        if (request.status() != null) {
            task.setStatus(request.status());
        }

        if (request.priority() != null) {
            task.setPriority(request.priority());
        }

        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        Task updated = taskRepository.save(task);

        String metadata = String.format(
                "{\"oldTitle\":\"%s\",\"newTitle\":\"%s\",\"oldStatus\":\"%s\",\"newStatus\":\"%s\",\"oldPriority\":\"%s\",\"newPriority\":\"%s\"}",
                oldTitle, task.getTitle(), oldStatus, task.getStatus(), oldPriority, task.getPriority()
        );

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.TASK_UPDATED,
                task.getProject().getWorkspace().getId(),
                task.getProject().getId(),
                task.getId(),
                "Task '" + oldTitle + "' updated",
                metadata
        );

        // 🔔 Notification (notify task owner)
        Long targetUserId = task.getCreatedBy();
        if (targetUserId != null && !targetUserId.equals(userId)) {
            notificationService.createNotification(
                    targetUserId,
                    NotificationType.TASK_UPDATED,
                    "Task '" + task.getTitle() + "' was updated",
                    task.getProject().getWorkspace().getId(),
                    task.getProject().getId(),
                    task.getId(),
                    null
            );
        }

        log.info("Task updated successfully id={}", updated.getId());
        return updated;
    }

    // PATCH TASK STATUS
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTaskStatus(Long id, TaskStatus status) {
        log.info("Updating task status id={} newStatus={}", id, status);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );
        TaskStatus oldStatus = task.getStatus();

        task.setStatus(status);

        Task updated = taskRepository.save(task);

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.TASK_STATUS_CHANGED ,
                task.getProject().getWorkspace().getId(),
                task.getProject().getId(),
                task.getId(),
                "Task status changed from " + oldStatus + " to " + status,
                "{\"oldStatus\":\"" + oldStatus + "\",\"newStatus\":\"" + status + "\"}"
        );

        // 🔔 Notification (notify task owner if exists)
        Long targetUserId = task.getCreatedBy();

        if (targetUserId != null && !targetUserId.equals(userId)) {
            notificationService.createNotification(
                    targetUserId,
                    NotificationType.TASK_STATUS_CHANGED,
                    "Task status updated to " + status,
                    task.getProject().getWorkspace().getId(),
                    task.getProject().getId(),
                    task.getId(),
                    null
            );
        }

        log.info("Task status updated successfully id={} status={}", updated.getId(), updated.getStatus());
        return updated;
    }

    // SOFT DELETE
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long id) {
        log.warn("Soft deleting task id={}", id);

        Task task = taskRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id
                        )
                );
        task.setDeleted(true);
        taskRepository.save(task);

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.TASK_DELETED,
                task.getProject().getWorkspace().getId(),
                task.getProject().getId(),
                task.getId(),
                "Task '" + task.getTitle() + "' soft deleted",
                null
        );
    }

    // RESTORE TASK
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
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

        Long userId = getCurrentUserId();
        activityService.logActivity(
                userId,
                ActivityAction.TASK_RESTORED,
                task.getProject().getWorkspace().getId(),
                task.getProject().getId(),
                task.getId(),
                "Task '" + task.getTitle() + "' restored from deleted state",
                null
        );

        log.info("Task restored successfully id={}", restored.getId());
        return restored;
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public Task assignTask(Long taskId, Long userIdToAssign) {
        log.info("Assigning task id={} to userId={}", taskId, userIdToAssign);

        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + taskId
                        )
                );

        Long workspaceId = task.getProject().getWorkspace().getId();

        Long currentUserId = getCurrentUserId();

        // 🔐 Role-based authorization (only OWNER, ADMIN can assign)
        var membership = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        if (!(membership.getRole() == WorkspaceRole.OWNER || membership.getRole() == WorkspaceRole.ADMIN)) {
            throw new AccessDeniedException("Only OWNER or ADMIN can assign tasks");
        }

        // Validate user (skip if unassign)
        if (userIdToAssign != null) {

            // 1. User must exist
            userRepository.findById(userIdToAssign)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with id: " + userIdToAssign
                            )
                    );

            // 2. User must belong to workspace
            workspaceMemberRepository
                    .findByWorkspaceIdAndUserId(workspaceId, userIdToAssign)
                    .orElseThrow(() ->
                            new AccessDeniedException(
                                    "User does not belong to this workspace"
                            )
                    );
        }

        Long previousAssignee = task.getAssignedTo();

        // No-op if same assignee
        if (Objects.equals(previousAssignee, userIdToAssign)) {
            log.info("No-op: task id={} already assigned to userId={}", taskId, userIdToAssign);
            return task;
        }

        // Allow unassign (userIdToAssign == null)
        task.setAssignedTo(userIdToAssign);
        Task updated = taskRepository.save(task);


        // Activity log
        String metadata = String.format(
                "{\"oldAssignee\":\"%s\",\"newAssignee\":\"%s\"}",
                String.valueOf(previousAssignee), String.valueOf(userIdToAssign)
        );

        activityService.logActivity(
                currentUserId,
                ActivityAction.TASK_ASSIGNED,
                task.getProject().getWorkspace().getId(),
                task.getProject().getId(),
                task.getId(),
                "Task '" + task.getTitle() + "' reassigned from " + previousAssignee + " to " + userIdToAssign,
                metadata
        );

        // Notification to assigned user
        if (userIdToAssign != null && !userIdToAssign.equals(currentUserId)) {
            notificationService.createNotification(
                    userIdToAssign,
                    NotificationType.TASK_ASSIGNED,
                    "You have been assigned to task '" + task.getTitle() + "'",
                    task.getProject().getWorkspace().getId(),
                    task.getProject().getId(),
                    task.getId(),
                    null
            );
        }

        log.info("Task assigned successfully id={} to userId={}", taskId, userIdToAssign);
        return updated;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}