package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserNotifications_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Test notification");
        notification.setUser(user);
        
        List<Notification> notifications = Arrays.asList(notification);
        
        when(userService.getUserById(1L)).thenReturn(user);
        when(notificationService.getAllNotifications(user)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/user/1/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifications[0].message").value("Test notification"));
    }

    @Test
    void getUserNotifications_UserNotFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/notifications/user/1/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNotification_Success() throws Exception {
        User user = new User();
        user.setId(1L);

        Notification notification = new Notification();
        notification.setMessage("New notification");
        notification.setUser(user);

        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setMessage("New notification");
        savedNotification.setUser(user);

        when(notificationService.createNotification(any(Notification.class))).thenReturn(savedNotification);

        mockMvc.perform(post("/api/notifications/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notification.id").value(1))
                .andExpect(jsonPath("$.message").value("Notification created successfully"));
    }

    @Test
    void getPendingNotifications_Success() throws Exception {
        User user = new User();
        user.setId(1L);

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Pending notification");
        notification.setUser(user);

        List<Notification> notifications = Arrays.asList(notification);

        when(userService.getUserById(1L)).thenReturn(user);
        when(notificationService.getPendingNotifications(user)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/user/1/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Pending notification"));
    }
}
