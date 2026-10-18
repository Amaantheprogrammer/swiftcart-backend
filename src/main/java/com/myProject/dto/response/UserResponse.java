package com.myProject.dto.response;

import java.io.Serializable;

import com.myProject.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder 
public class UserResponse implements Serializable {
    private Long id;
    private String name;
    private String email;
    private Role role;
}