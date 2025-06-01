package com.example.volunteer_platform.config;

import com.example.volunteer_platform.entity.Role;
import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        for (RoleType roleType : RoleType.values()) {
            Role role = roleRepository.findByRoleName(roleType);
            if (role == null) {
                role = new Role();
                role.setRoleName(roleType);
                roleRepository.save(role);
                System.out.println("Initialized role: " + roleType);
            }
        }
    }
}