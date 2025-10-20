package com.example.demo.repository;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndDeletedFalse(User user);
    List<Task> findByUserAndCompletedTrue(User user);
    List<Task> findByTargetDateBeforeAndCompletedFalseAndDeletedFalse(LocalDateTime date);
}
