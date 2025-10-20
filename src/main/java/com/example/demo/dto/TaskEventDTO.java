package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEventDTO {
    private Long taskId;
    private String title;
    private String description;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private String eventType;
}
