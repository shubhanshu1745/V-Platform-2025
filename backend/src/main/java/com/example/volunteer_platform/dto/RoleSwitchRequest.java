package com.example.volunteer_platform.dto;

import com.example.volunteer_platform.enums.RoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleSwitchRequest {
    private RoleType role;
}