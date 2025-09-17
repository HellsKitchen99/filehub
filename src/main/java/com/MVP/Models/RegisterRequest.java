package com.MVP.Models;

import lombok.Data;

@Data
public class RegisterRequest {
    
    public RegisterRequest(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    private String username;
    private String password;
    private Role role;
}
