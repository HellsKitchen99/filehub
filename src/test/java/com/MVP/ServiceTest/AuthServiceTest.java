package com.MVP.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.MVP.Config.JwtUtil;
import com.MVP.Models.LoginRequest;
import com.MVP.Models.RegisterRequest;
import com.MVP.Models.Role;
import com.MVP.Repository.UserRepository;
import com.MVP.Service.AuthService;

import com.MVP.Models.User;
import com.MVP.Models.UserDTO;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    
    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private String secretKey = "SECRET_KEY_VERYLONGSECRETKEY16138352";


    @Test
    public void registerTest_Success() {
        //preparing
        RegisterRequest registerRequest = new RegisterRequest("username", "password", Role.USER);
        UserDTO user = new UserDTO("username", "password", Role.USER);
        when(userRepository.getUserFromDataBase("username")).thenReturn(Optional.empty());
        when(userRepository.addUserToDataBase(user)).thenReturn(true);
        when(jwtUtil.generateJwtTokenForRegistry("username", Role.USER)).thenReturn("token12345");

        //act
        String token = authService.register(registerRequest);
        
        //assert
        if (!token.equals("token12345")) {
            fail("тест упал - пришел неверный токен");
        }
    }

    @Test
    public void registerTest_Failure_UserAlreadyExists() {
        //preparing
        RegisterRequest registerRequest = new RegisterRequest("username", "password", Role.USER);
        UserDTO user = new UserDTO("username", "password", Role.USER);
        User userForCheck = new User(0, secretKey, secretKey, Role.USER);
        when(userRepository.getUserFromDataBase("username")).thenReturn(Optional.of(userForCheck));

        //act + assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));

        assertEquals("Пользователь уже существует", ex.getMessage());
    }

    @Test
    public void registerTest_Failure_AddToDataBaseError() {
        //preparing
        RegisterRequest registerRequest = new RegisterRequest("username", "password", Role.USER);
        UserDTO user = new UserDTO("username", "password", Role.USER);
        when(userRepository.getUserFromDataBase("username")).thenReturn(Optional.empty());
        when(userRepository.addUserToDataBase(user)).thenReturn(false);

        //act
        String token = authService.register(registerRequest);

        //assert
        if (!token.equals("database error")) {
            fail("тест упал - пришел неверный ответ");
        }
    }

    @Test
    public void loginTest_Success() {
        //preparing
        LoginRequest loginRequest = new LoginRequest("username", "password", Role.USER);
        User user = new User(1, "username", "password", Role.USER);
        Collection<? extends GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication mockedAuthentication = new UsernamePasswordAuthenticationToken(user, null, roles);
        when(authManager.authenticate(any())).thenReturn(mockedAuthentication);
        when(jwtUtil.generateJwtToken(mockedAuthentication)).thenReturn("mockedToken");

        //act
        String token = authService.login(loginRequest);

        //assert
        if (!token.equals("mockedToken")) {
            fail("тест упал - пришел неверный ответ");
        }
    }
}
