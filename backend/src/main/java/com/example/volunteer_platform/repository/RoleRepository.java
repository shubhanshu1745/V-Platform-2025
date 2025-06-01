package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Role;
import com.example.volunteer_platform.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(RoleType roleName);
}