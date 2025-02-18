package com.prashanth.taskmanagementsystem.model.mapper;

import com.prashanth.taskmanagementsystem.model.dto.CommentDTO;
import com.prashanth.taskmanagementsystem.model.entity.Comment;
import com.prashanth.taskmanagementsystem.model.entity.Task;
import com.prashanth.taskmanagementsystem.model.entity.User;

public class CommentMapper {

    public static CommentDTO toCommentDTO(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setUpdatedAt(comment.getUpdatedAt());


        commentDTO.setUserId(comment.getUser().getId()!=null?comment.getUser().getId():null);
        commentDTO.setTaskId(comment.getTask().getId()!=null?comment.getTask().getId():null);

        return commentDTO;
    }

    public static Comment toComment(CommentDTO commentDTO) {
        if (commentDTO == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(commentDTO.getCreatedAt());
        comment.setUpdatedAt(commentDTO.getUpdatedAt());

        // User conversion
        if (commentDTO.getUserId() != null) {
            User user = new User();
            user.setId(commentDTO.getUserId());
            comment.setUser(user);
        }

        // Task conversion
        if (commentDTO.getTaskId() != null) {
            Task task = new Task();
            task.setId(commentDTO.getTaskId());
            comment.setTask(task);
        }
        return comment;
    }
}

