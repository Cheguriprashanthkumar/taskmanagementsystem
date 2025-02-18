package com.prashanth.taskmanagementsystem.repository;

import com.prashanth.taskmanagementsystem.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskId(Long taskId);
    List<Comment> findByUserId(Long userId);

}
