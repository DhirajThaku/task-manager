package com.taskmanager.backendnew.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.taskmanager.backendnew.entity.Role;
import com.taskmanager.backendnew.repository.RoleRepository;

@Component
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // this runs the first
    @Override
    public void run(String... args) { // Spring calls this automatically on startup
        seed("ROLE_USER");
        seed("ROLE_ADMIN");
    }

    // if the no role is present previously in the user roles stable then it adds
    // the by default
    private void seed(String name) {
        if (roleRepository.findByName(name).isEmpty()) { // only insert if missing
            Role role = new Role();
            role.setName(name);
            roleRepository.save(role);
        }
    }
}
