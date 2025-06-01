package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByPincode(String pincode);
    List<User> findByPincodeAndRoles_RoleName(String pincode, String roleName);
    List<User> findByRoles_RoleName(RoleType roleType);
}