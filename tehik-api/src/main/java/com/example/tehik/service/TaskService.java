package com.example.tehik.service;

import com.example.tehik.dto.TaskResponseDTO;
import com.example.tehik.mappers.TaskMapper;
import com.example.tehik.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public Page<TaskResponseDTO> getTasksByIp(String ip, Pageable pageable){
        return taskRepository.findAllByIp(ip, pageable).map(taskMapper::toResponseDTO);

    }
}
