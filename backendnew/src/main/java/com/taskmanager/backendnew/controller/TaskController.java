package com.taskmanager.backendnew.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.backendnew.dto.TaskRequest;
import com.taskmanager.backendnew.dto.TaskResponse;
import com.taskmanager.backendnew.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<TaskResponse> getMyTasks(Authentication authentication) {
        return taskService.getTasksForUser(authentication.getName())
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id, Authentication authentication) {
        return TaskResponse.from(taskService.getTaskById(id, authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        TaskResponse created = TaskResponse.from(
                taskService.createTask(request, authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        return TaskResponse.from(
                taskService.updateTask(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
            Authentication authentication) {
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/schedule")
    public List<TaskResponse> getSchedule(Authentication auth) {

        return taskService.getScheduledTasks(auth.getName()).stream().map(TaskResponse::from).toList();
    }
}
