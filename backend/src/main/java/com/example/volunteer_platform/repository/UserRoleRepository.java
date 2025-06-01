//package com.example.volunteer_platform.repository;
//
//import com.example.volunteer_platform.entity.User;
//import com.example.volunteer_platform.entity.UserRole;
//import com.example.volunteer_platform.entity.UserRoleId;
//import jakarta.transaction.Transactional;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//
//public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
//    @Transactional
//    @Modifying
//    @Query("delete from UserRole ur where ur.user = :user")
//    void deleteByUser(User user);
//}