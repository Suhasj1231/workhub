package com.smj.workhub.task.repository;

import com.smj.workhub.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends
        JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    // Find active task by ID
    Optional<Task> findByIdAndDeletedFalse(Long id);

    // Fetch task with project and workspace to avoid LazyInitializationException
    @Query("""
        SELECT t FROM Task t
        JOIN FETCH t.project p
        JOIN FETCH p.workspace
        WHERE t.id = :id AND t.deleted = false
    """)
    Optional<Task> findByIdWithProjectAndWorkspace(Long id);

}
