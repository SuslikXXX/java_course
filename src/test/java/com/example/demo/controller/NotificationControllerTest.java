package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/notifications/create creates notification")
    void createNotification_success() throws Exception {
        User usr = User.builder().id(1L).username("u").email("u@e.com").password("p").build();
        Notification saved = Notification.builder()
                .id(10L)
                .message("Hi")
                .user(usr)
                .read(false)
                .creationDate(LocalDateTime.now())
                .build();
        when(notificationService.createNotification(any(Notification.class))).thenReturn(saved);

        String body = "{" +
                "\"message\":\"Hi\"," +
                "\"user\": { \"id\": 1 }" +
                "}";

        mockMvc.perform(post("/api/notifications/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notification.id").value(10))
                .andExpect(jsonPath("$.notification.message").value("Hi"));
    }

    @Test
    @DisplayName("GET /api/notifications/user/{id}/all user not found -> 404")
    void getAll_userNotFound() throws Exception {
        when(userService.getUserById(5L)).thenReturn(null);
        mockMvc.perform(get("/api/notifications/user/5/all"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    @DisplayName("GET /api/notifications/user/{id}/all returns list")
    void getAll_success() throws Exception {
        User usr = User.builder().id(1L).build();
        when(userService.getUserById(1L)).thenReturn(usr);
        when(notificationService.getAllNotifications(usr)).thenReturn(List.of(new Notification(), new Notification()));
        mockMvc.perform(get("/api/notifications/user/1/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifications").isArray())
                .andExpect(jsonPath("$.notifications[0]").exists());
    }

    @Test
    @DisplayName("GET /api/notifications/user/{id}/pending returns list")
    void getPending_success() throws Exception {
        User usr = User.builder().id(1L).build();
        when(userService.getUserById(1L)).thenReturn(usr);
        when(notificationService.getPendingNotifications(usr)).thenReturn(List.of(new Notification()));
        mockMvc.perform(get("/api/notifications/user/1/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("GET /api/notifications/user/{id}/pending user not found -> 404")
    void getPending_userNotFound() throws Exception {
        when(userService.getUserById(9L)).thenReturn(null);
        mockMvc.perform(get("/api/notifications/user/9/pending"))
                .andExpect(status().isNotFound());
    }
}
