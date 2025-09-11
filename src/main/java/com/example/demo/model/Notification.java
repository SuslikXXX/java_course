package com.example.demo.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    private Long id;

    private String message;

    private LocalDateTime creationDate;

    private boolean read;

    private User user;

    public Notification(String message, User user) {
        this.message = message;
        this.user = user;
        this.creationDate = LocalDateTime.now();
        this.read = false;
    }

}