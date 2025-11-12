package com.example.tehik.service;

import com.example.tehik.dto.TaskResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskStatusUpdateService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendUpdate(TaskResponseDTO taskDto) {
        String destination = "/topic/task-status/" + taskDto.id();
        log.info("Sending task update to WebSocket destination: {} with status: {}", destination, taskDto.status());

        messagingTemplate.convertAndSend(destination, taskDto);
    }
}
