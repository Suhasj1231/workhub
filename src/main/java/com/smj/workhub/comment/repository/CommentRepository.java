package com.smj.workhub.comment.repository;

import com.smj.workhub.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Fetch comments for a task (excluding deleted)
    Page<Comment> findByTaskIdAndDeletedFalse(Long taskId, Pageable pageable);

}
