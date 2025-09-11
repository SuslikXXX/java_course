package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<Map<String, Object>> getUserNotifications(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationService.getAllNotifications(user));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(notificationService.getPendingNotifications(user));
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> addNotification(@RequestBody Notification notification) {
        try {
            Notification saved = notificationService.createNotification(notification);
            Map<String, Object> response = new HashMap<>();
            response.put("notification", saved);
            response.put("message", "Notification created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}