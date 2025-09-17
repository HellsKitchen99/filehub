package com.MVP.Controllers;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MVP.Models.LoginRequest;
import com.MVP.Models.RegisterRequest;
import com.MVP.Service.AuthService;

@RestController
@RequestMapping("/authentication")
public class AuthController {

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerController(@RequestBody RegisterRequest registerRequest) {
        String token = authService.register(registerRequest);
        Map<String, String> response = new HashMap<>();
        if (token == "database error") {
            response.put("error", "ошибка добавления пользователя в базу");
            return ResponseEntity.status(500).body(response);
        }
        response.put("token", token);
        return ResponseEntity.status(201).body(response);
    }
    
    @PostMapping("/login") 
    public ResponseEntity<?> loginController(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.status(200).body(response);
    }
}
