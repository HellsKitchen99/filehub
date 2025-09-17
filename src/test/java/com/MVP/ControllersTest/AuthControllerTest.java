package com.MVP.ControllersTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.MVP.Config.JwtUtil;
import com.MVP.Controllers.AuthController;
import com.MVP.Models.LoginRequest;
import com.MVP.Models.RegisterRequest;
import com.MVP.Models.Role;
import com.MVP.Service.AuthService;
import com.MVP.Service.CustomUserDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void registerControllerTest_Success() {
        //preparing
        RegisterRequest registerRequest = new RegisterRequest("username", "password", Role.USER);
        when(authService.register(registerRequest)).thenReturn("да что мне скверные виды этого города мертвых");
        ObjectMapper mapper = new ObjectMapper();
        byte[] registerRequestBytes = null;
        try {
            registerRequestBytes = mapper.writeValueAsBytes(registerRequest);
        } catch (JsonProcessingException ex) {
            fail();
        }

        //act
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/authentication/register")
            .contentType("application/json")
            .content(registerRequestBytes))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn();
        } catch (Exception ex) {
            fail();
        }

        //assert
        String resultAsString = null;
        try {
            resultAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException ex) {
            fail();
        }
        System.out.println(">>> " + resultAsString + " <<<");
        if (!resultAsString.contains("token") || !resultAsString.contains("да что мне скверные виды этого города мертвых")) {
            fail();
        }
    }

    @Test
    public void registerControllerTest_Failure() {
        //preparing
        byte[] badRegisterRequest = "{'username': 123}".getBytes();

        //act
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/authentication/register")
            .contentType("application/json")
            .content(badRegisterRequest))
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.error").isNotEmpty())
            .andReturn();
        } catch (Exception ex) {
            fail();
        }

        //assert
        String resultAsString = null;
        try {
            resultAsString = result.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException ex) {
            fail();
        }
        if (!resultAsString.contains("error")) {
            fail();
        }
    }
    
    @Test
    public void loginControllerTest_Success() {
        //preparing
        LoginRequest loginRequest = new LoginRequest("username", "password", Role.USER);
        ObjectMapper mapper = new ObjectMapper();
        byte[] loginRequestBytes = null;
        try {
            loginRequestBytes = mapper.writeValueAsBytes(loginRequest);
        } catch (JsonProcessingException ex) {
            fail("тест упал - " + ex.getMessage());
        }
        String token = "я просто взял разбег";
        when(authService.login(loginRequest)).thenReturn(token);

        //act
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/authentication/login")
            .contentType("application/json")
            .content(loginRequestBytes))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn();
        } catch (Exception ex) {
            fail("тест упал - " + ex.getMessage());
        }

        //assert
        String resultAsString = null;
        try {
            resultAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException ex) {
            fail("тест упал - " + ex.getMessage());
        }

        System.out.println(">>> " + resultAsString + " <<<");

        if (!resultAsString.contains("я просто взял разбег")) {
            fail("тест упал - пришло неверное тело ответа");
        }
    }

    @Test
    public void loginControllerTest_Failure() {
        //preparing
        byte[] bitiyJson = "{'username': 123}".getBytes();

        //act
        MvcResult result = null;
        try {
            result = mockMvc.perform(post("/authentication/login")
            .contentType("application/json")
            .content(bitiyJson))
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.error").isNotEmpty())
            .andReturn();
        } catch (Exception ex) {
            fail("тест упал - " + ex.getMessage());
        }

        //assert
        String resultAsString = null;
        try {
            resultAsString = result.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException ex) {
            fail("тест упал - " + ex.getMessage());
        }

        if (!resultAsString.contains("error")) {
            fail("тест упал - пришло неверное тело ответа");
        }
    }
}

