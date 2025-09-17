package com.MVP.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.MVP.Config.JwtUtil;
import com.MVP.Models.LoginRequest;
import com.MVP.Models.RegisterRequest;
import com.MVP.Models.User;
import com.MVP.Models.UserDTO;
import com.MVP.Repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public AuthService(AuthenticationManager authManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    //логика регистрации
    public String register(RegisterRequest registerRequest) {
        UserDTO user = new UserDTO(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getRole());

        //проверка пользователя в базе
        User userForCheck = userRepository.getUserFromDataBase(user.getUsername()).orElse(null);
        if (userForCheck != null) {
            throw new RuntimeException("Пользователь уже существует");
        }

        //добавление пользователя в базу
        boolean isAdded = userRepository.addUserToDataBase(user);

        if (isAdded == false) {
            return "database error";
        }

        //генерим токен
        String token = jwtUtil.generateJwtTokenForRegistry(user.getUsername(), user.getRole());
        return token;
    }
    
    //логика входа
    public String login(LoginRequest loginRequest) {
        //создаем объект UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authenticator = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        //передаем в AuthenticationManager
        Authentication authentication = authManager.authenticate(authenticator);

        //генерация jwt
        String token = jwtUtil.generateJwtToken(authentication);
        return token;
    }
}
