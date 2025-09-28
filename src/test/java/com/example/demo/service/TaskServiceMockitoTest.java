package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.impl.InMemoryTaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceMockitoTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private InMemoryTaskService taskService;

    @Test
    @DisplayName("createTask: saves and returns task")
    void createTask_success() {
        User user = User.builder().id(1L).username("u").email("u@e.com").password("p").build();
        Task input = Task.builder()
                .title("Title")
                .description("Desc")
                .targetDate(LocalDateTime.now().plusDays(1))
                .user(user)
                .build();

        Task saved = Task.builder()
                .id(10L)
                .title("Title")
                .description("Desc")
                .targetDate(input.getTargetDate())
                .user(user)
                .completed(false)
                .deleted(false)
                .creationDate(LocalDateTime.now())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.createTask(input);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.isCompleted()).isFalse();
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Title");
    }

    @Test
    @DisplayName("getAllTasks: returns all from repository")
    void getAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(new Task(), new Task(), new Task()));
        assertThat(taskService.getAllTasks()).hasSize(3);
    }

    @Test
    @DisplayName("getAllTasks(user): filters deleted=false")
    void getAllTasksByUser() {
        User user = User.builder().id(2L).build();
        when(taskRepository.findByUserAndDeletedFalse(user)).thenReturn(List.of(new Task(), new Task()));
        assertThat(taskService.getAllTasks(user)).hasSize(2);
    }

    @Test
    @DisplayName("getPendingTasks: excludes completed tasks")
    void getPendingTasks() {
        User user = User.builder().id(3L).build();
        Task t1 = Task.builder().id(1L).completed(false).deleted(false).build();
        Task t2 = Task.builder().id(2L).completed(true).deleted(false).build();
        when(taskRepository.findByUserAndDeletedFalse(user)).thenReturn(List.of(t1, t2)); // t3 отфильтрован раньше
        assertThat(taskService.getPendingTasks(user)).extracting(Task::getId).containsExactly(1L);
    }

    @Test
    @DisplayName("getTaskById: found returns task")
    void getTaskById_found() {
        Task t = Task.builder().id(5L).title("T").build();
        when(taskRepository.findById(5L)).thenReturn(Optional.of(t));
        assertThat(taskService.getTaskById(5L)).isNotNull();
    }

    @Test
    @DisplayName("getTaskById: not found returns null")
    void getTaskById_notFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(taskService.getTaskById(99L)).isNull();
    }

    @Test
    @DisplayName("deleteTask: calls repository")
    void deleteTask() {
        taskService.deleteTask(11L);
        verify(taskRepository).deleteById(11L);
    }
}
