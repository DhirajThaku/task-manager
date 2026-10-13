package com.taskmanager.backendnew.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanager.backendnew.dto.TaskRequest;
import com.taskmanager.backendnew.entity.Task;
import com.taskmanager.backendnew.entity.User;
import com.taskmanager.backendnew.exception.TaskNotFoundException;
import com.taskmanager.backendnew.repository.TaskRepository;
import com.taskmanager.backendnew.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Task> getTasksForUser(String username) {
        User user = getUser(username);
        return taskRepository.findByOwnerId(user.getId());
    }

    @Transactional
    public Task createTask(TaskRequest request, String username) {
        User owner = getUser(username);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        task.setOwner(owner); // the owner comes from the JWT, never from the request body

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, TaskRequest request, String username) {
        Task task = findOwnedTask(taskId, username);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(Long taskId, String username) {
        return findOwnedTask(taskId, username); // 404 if missing or not yours
    }

    // to get the all the tasks for user and in the order way
    @Transactional(readOnly = true)
    public List<Task> getScheduledTasks(String username) {

        return TaskScheduler.schedule(getTasksForUser(username), LocalDateTime.now());

    }

    @Transactional
    public void deleteTask(Long taskId, String username) {
        Task task = findOwnedTask(taskId, username);
        taskRepository.delete(task);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // The single place where "you can only touch your own tasks" is enforced
    private Task findOwnedTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (!task.getOwner().getUsername().equals(username)) {
            throw new TaskNotFoundException(taskId); // same message on purpose
        }
        return task;
    }
}
