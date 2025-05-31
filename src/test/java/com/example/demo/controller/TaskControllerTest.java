package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUser(user);

        when(userService.getUserById(1L)).thenReturn(user);
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void getAllTasks_Success() throws Exception {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");

        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void getTasksByUser_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("User Task");
        task.setUser(user);

        List<Task> tasks = Arrays.asList(task);

        when(userService.getUserById(1L)).thenReturn(user);
        when(taskService.getAllTasks(user)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("User Task"));
    }

    @Test
    void getPendingTasks_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Pending Task");
        task.setUser(user);
        task.setCompleted(false);

        List<Task> tasks = Arrays.asList(task);

        when(userService.getUserById(1L)).thenReturn(user);
        when(taskService.getPendingTasks(user)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/user/1/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Pending Task"));
    }

    @Test
    void getTaskById_Success() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");

        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void getTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_Success() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_NotFound() throws Exception {
        when(taskService.getTaskById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNoContent());
    }
} 