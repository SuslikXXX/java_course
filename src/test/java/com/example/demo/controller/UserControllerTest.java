package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users/register returns 201 with user")
    void register_success() throws Exception {
        User saved = User.builder().id(1L).username("alice").email("alice@example.com").password("p").build();
        when(userService.registerUser(any(User.class))).thenReturn(saved);

        String body = "{" +
                "\"username\":\"alice\"," +
                "\"email\":\"alice@example.com\"," +
                "\"password\":\"p\"" +
                "}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("alice"));
    }

    @Test
    @DisplayName("POST /api/users/register duplicate returns 400")
    void register_duplicate() throws Exception {
        when(userService.registerUser(any(User.class))).thenThrow(new IllegalArgumentException("Username already exists"));

        String body = "{" +
                "\"username\":\"alice\"," +
                "\"email\":\"alice@example.com\"," +
                "\"password\":\"p\"" +
                "}";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    @Test
    @DisplayName("POST /api/users/login success")
    void login_success() throws Exception {
        User existing = User.builder().id(2L).username("bob").password("secret").email("b@e.com").build();
        when(userService.login("bob", "secret")).thenReturn(existing);

        mockMvc.perform(post("/api/users/login?username=bob&password=secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("POST /api/users/login invalid returns 401")
    void login_invalid() throws Exception {
        when(userService.login("bob", "bad")).thenReturn(null);

        mockMvc.perform(post("/api/users/login?username=bob&password=bad"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    @DisplayName("GET /api/users/{id} not found returns 404")
    void getUser_notFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/{id} returns user")
    void getUser_found() throws Exception {
        User existing = User.builder().id(5L).username("x").email("x@e.com").password("p").build();
        when(userService.getUserById(5L)).thenReturn(existing);
        mockMvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.username").value("x"));
    }
}
