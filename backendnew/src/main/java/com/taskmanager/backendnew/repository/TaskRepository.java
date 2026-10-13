package com.taskmanager.backendnew.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.backendnew.entity.Task;
import com.taskmanager.backendnew.entity.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(User owner);

    List<Task> findByOwnerId(Long ownerId);

}
