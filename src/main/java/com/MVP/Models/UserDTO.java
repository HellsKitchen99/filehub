package com.MVP.Models;

import lombok.Data;

@Data
public class UserDTO {
    
    public UserDTO(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    private String username;
    private String password;
    private Role role;
}
