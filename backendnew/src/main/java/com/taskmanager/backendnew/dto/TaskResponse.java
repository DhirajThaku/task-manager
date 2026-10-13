package com.taskmanager.backendnew.dto;

import java.time.LocalDateTime;

import com.taskmanager.backendnew.entity.Task;
import com.taskmanager.backendnew.entity.TaskPriority;
import com.taskmanager.backendnew.entity.TaskStatus;

import lombok.Getter;

@Getter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDateTime createdAt;

    private TaskResponse() {

    }

    public static TaskResponse from(Task task) {
        TaskResponse r = new TaskResponse();
        r.id = task.getId();
        r.title = task.getTitle();
        r.description = task.getDescription();
        r.deadline = task.getDeadline();
        r.priority = task.getPriority();
        r.status = task.getStatus();
        r.createdAt = task.getCreatedAt();
        return r;
    }

}
