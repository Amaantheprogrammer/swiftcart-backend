package com.swiftcart.dto.response;

import java.io.Serializable;

import com.swiftcart.enums.Role;

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