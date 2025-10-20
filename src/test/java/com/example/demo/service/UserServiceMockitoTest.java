package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.InMemoryUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceMockitoTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InMemoryUserService userService;

    @Test
    @DisplayName("registerUser: success saves and returns user")
    void registerUser_success() {
        User input = User.builder()
                .username("alice")
                .email("alice@example.com")
                .password("pwd")
                .build();

        User saved = User.builder()
                .id(10L)
                .username("alice")
                .email("alice@example.com")
                .password("pwd")
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.registerUser(input);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getUsername()).isEqualTo("alice");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("alice@example.com");
        verify(userRepository).findByUsername("alice");
    }

    @Test
    @DisplayName("registerUser: duplicate username throws")
    void registerUser_duplicate() {
        User input = User.builder()
                .username("alice")
                .email("alice@example.com")
                .password("pwd")
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.registerUser(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login: success returns user")
    void login_success() {
        User existing = User.builder()
                .id(1L)
                .username("bob")
                .password("secret")
                .email("b@ex.com")
                .build();

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(existing));

        User result = userService.login("bob", "secret");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("login: wrong password returns null")
    void login_wrongPassword() {
        User existing = User.builder()
                .id(1L)
                .username("bob")
                .password("secret")
                .email("b@ex.com")
                .build();
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(existing));

        User result = userService.login("bob", "bad");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("login: user not found returns null")
    void login_userNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        User result = userService.login("ghost", "pwd");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getUserById: found returns user")
    void getUserById_found() {
        User existing = User.builder().id(5L).username("x").email("x@e.com").password("p").build();
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        User result = userService.getUserById(5L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("getUserById: not found returns null")
    void getUserById_notFound() {
        when(userRepository.findById(77L)).thenReturn(Optional.empty());
        User result = userService.getUserById(77L);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getAllUsers: returns list from repository")
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
        assertThat(userService.getAllUsers()).hasSize(2);
    }
}
