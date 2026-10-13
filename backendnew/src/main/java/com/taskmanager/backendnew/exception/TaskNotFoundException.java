package com.taskmanager.backendnew.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task not found with id" + id);
    }
}
