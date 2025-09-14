package com.example.demo.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "is_read")
    private boolean read;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Notification(String message, User user) {
        this.message = message;
        this.user = user;
        this.creationDate = LocalDateTime.now();
        this.read = false;
    }

}