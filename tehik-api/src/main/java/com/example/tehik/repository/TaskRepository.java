package com.example.tehik.repository;

import com.example.tehik.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByIpOrderByCreatedAtDesc(String ip);
}
