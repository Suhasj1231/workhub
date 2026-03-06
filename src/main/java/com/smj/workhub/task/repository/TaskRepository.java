package com.smj.workhub.task.repository;

import com.smj.workhub.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TaskRepository extends
        JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    // Find active task by ID
    Optional<Task> findByIdAndDeletedFalse(Long id);

}
