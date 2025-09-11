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
public class Task {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime targetDate;

    private boolean completed;

    private boolean deleted;

    private User user;

    public Task(String title, String description, LocalDateTime targetDate, User user) {
        this.title = title;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.targetDate = targetDate;
        this.completed = false;
        this.deleted = false;
        this.user = user;
    }

} 