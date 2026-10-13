package com.taskmanager.backendnew.repository;

import com.taskmanager.backendnew.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // we know it might be null so used optional to avoid the forget of null checks
    // where we are calling this method
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
