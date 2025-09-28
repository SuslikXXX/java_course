package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/tasks creates task")
    void createTask_success() throws Exception {
        User usr = User.builder().id(1L).username("u").email("u@e.com").password("p").build();
        Task saved = Task.builder()
                .id(10L)
                .title("Task A")
                .description("Desc")
                .user(usr)
                .creationDate(LocalDateTime.now())
                .completed(false)
                .deleted(false)
                .build();
        when(taskService.createTask(any(Task.class))).thenReturn(saved);

        String body = "{" +
                "\"title\":\"Task A\"," +
                "\"description\":\"Desc\"," +
                "\"user\": { \"id\": 1 }" +
                "}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Task A"));
    }

    @Test
    @DisplayName("GET /api/tasks returns list")
    void getAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(new Task(), new Task()));
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} not found -> 404")
    void getTask_notFound() throws Exception {
        when(taskService.getTaskById(77L)).thenReturn(null);
        mockMvc.perform(get("/api/tasks/77"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns task")
    void getTask_found() throws Exception {
        Task t = Task.builder().id(3L).title("X").build();
        when(taskService.getTaskById(3L)).thenReturn(t);
        mockMvc.perform(get("/api/tasks/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{id} user not found -> 404")
    void getTasksByUser_userNotFound() throws Exception {
        when(userService.getUserById(5L)).thenReturn(null);
        mockMvc.perform(get("/api/tasks/user/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/user/{id} returns tasks")
    void getTasksByUser_found() throws Exception {
        User usr = User.builder().id(1L).build();
        when(userService.getUserById(1L)).thenReturn(usr);
        when(taskService.getAllTasks(usr)).thenReturn(List.of(new Task()));
        mockMvc.perform(get("/api/tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("GET /api/tasks/user/{id}/pending returns tasks")
    void getPendingTasks() throws Exception {
        User usr = User.builder().id(1L).build();
        when(userService.getUserById(1L)).thenReturn(usr);
        when(taskService.getPendingTasks(usr)).thenReturn(List.of(new Task()));
        mockMvc.perform(get("/api/tasks/user/1/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} returns 204")
    void deleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/9"))
                .andExpect(status().isNoContent());
    }
}
