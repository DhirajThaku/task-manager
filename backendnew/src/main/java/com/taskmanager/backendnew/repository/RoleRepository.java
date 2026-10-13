package com.taskmanager.backendnew.repository;

import java.util.Optional;

import com.taskmanager.backendnew.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
