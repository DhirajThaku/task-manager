package com.taskmanager.backendnew.dto;

import java.time.LocalDateTime;

import com.taskmanager.backendnew.entity.TaskPriority;
import com.taskmanager.backendnew.entity.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {
    // client sends
    @NotBlank(message = "Title should be present")
    @Size(max = 100, message = "Title should be at most 100 characters")
    private String title;

    @Size(max = 1000, message = "Description at most has 1000 characters")
    private String description;
    private LocalDateTime deadline;

    @NotNull(message = "Priority required!")
    private TaskPriority priority;
    private TaskStatus status; // optional on create ,defaults to PENDING
}
