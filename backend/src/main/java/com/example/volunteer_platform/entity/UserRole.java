//package com.example.volunteer_platform.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.NoArgsConstructor;
//import lombok.AllArgsConstructor;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "user_roles")
//@IdClass(UserRoleId.class)
//public class UserRole {
//    @Id
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @Id
//    @ManyToOne
//    @JoinColumn(name = "role_id")
//    private Role role;
//}