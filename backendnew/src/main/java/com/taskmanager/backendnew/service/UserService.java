package com.taskmanager.backendnew.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskmanager.backendnew.dto.RegisterRequest;
import com.taskmanager.backendnew.entity.Role;
import com.taskmanager.backendnew.entity.User;
import com.taskmanager.backendnew.exception.EmailAlreadyExistsException;
import com.taskmanager.backendnew.exception.UsernameAlreadyExistsException;
import com.taskmanager.backendnew.repository.RoleRepository;

import com.taskmanager.backendnew.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userrepository;

    @Autowired
    private PasswordEncoder passencoder;

    @Autowired
    private RoleRepository rolerepository;

    @Transactional
    public User registerUser(RegisterRequest request) {

        if (userrepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        if (userrepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // now save the data
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // dont save password directly
        user.setPassword(passencoder.encode(request.getPassword()));

        // using this getting the default role from the roles table
        Role defaultRole = rolerepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("there is no default role is given."));

        // and then setting the roles for particular user one user can have multiple
        // roles
        user.setRoles(Set.of(defaultRole));

        return userrepository.save(user);

    }

}
