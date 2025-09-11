package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    private String username;

    private String email;

    private String password;

      public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
} 