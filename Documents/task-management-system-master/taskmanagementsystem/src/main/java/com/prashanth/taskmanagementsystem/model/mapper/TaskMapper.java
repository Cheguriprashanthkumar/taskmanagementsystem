package com.prashanth.taskmanagementsystem.model.mapper;

import com.prashanth.taskmanagementsystem.model.dto.TaskDTO;
import com.prashanth.taskmanagementsystem.model.entity.Comment;
import com.prashanth.taskmanagementsystem.model.entity.Task;
import com.prashanth.taskmanagementsystem.model.entity.User;
import com.prashanth.taskmanagementsystem.model.enums.TaskPriority;
import com.prashanth.taskmanagementsystem.model.enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static TaskDTO toTaskDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getTaskStatus().name());
        taskDTO.setCreatedAt(task.getCreatedAt());
        taskDTO.setUpdatedAt(task.getUpdatedAt());
        taskDTO.setPriority(task.getTaskPriority() != null ? task.getTaskPriority().name() : null);

        taskDTO.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null);

        taskDTO.setCommentIds(task.getComments() != null ? task.getComments().stream()
                .map(Comment::getId)
                .collect(Collectors.toList())
                : new ArrayList<>());



        return taskDTO;
    }

    public static Task toTask(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        Task task = new Task();
        task.setId(taskDTO.getId());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setCreatedAt(taskDTO.getCreatedAt());
        task.setUpdatedAt(taskDTO.getUpdatedAt());

        if (taskDTO.getStatus() != null) {
            try {
                task.setTaskStatus(TaskStatus.valueOf(taskDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                // TODO: For the wrong ENUM handle appropiately
                throw new RuntimeException("Invalid status value: " + taskDTO.getStatus());
            }
        }

        if (taskDTO.getPriority() != null) {
            task.setTaskPriority(TaskPriority.valueOf(taskDTO.getPriority()));
        }

        // Assignee conversion
        if (taskDTO.getAssigneeId() != null) {
            User assignee = new User();
            assignee.setId(taskDTO.getAssigneeId());
            task.setAssignee(assignee);
        }

        //Comment IDs -> Comments
        if (taskDTO.getCommentIds() != null) {
            List<Comment> comments = taskDTO.getCommentIds().stream()
                    .map(commentId->{
                        Comment comment = new Comment();
                        comment.setId(commentId);
                        return comment;
                    })
                    .collect(Collectors.toList());
            task.setComments(comments);
        }

        return task;
    }
}
