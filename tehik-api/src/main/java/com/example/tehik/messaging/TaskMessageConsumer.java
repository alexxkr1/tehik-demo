package com.example.tehik.messaging;

import com.example.tehik.dto.TaskResponseDTO;
import com.example.tehik.entity.Task;
import com.example.tehik.enums.TaskStatus;
import com.example.tehik.mappers.TaskMapper;
import com.example.tehik.repository.TaskRepository;
import com.example.tehik.service.TaskStatusUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskMessageConsumer {

    private final TaskRepository taskRepository;
    private  final TaskStatusUpdateService taskStatusUpdateService;
    private final TaskMapper taskMapper;

    @RabbitListener(queues = {"${app.rabbitmq.queue}"})
    public void receiveMessage(Long taskId) {
        log.info("Received task ID: {}. Starting processing.", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));


        // Wait 10 seconds for development purposes to test working with websocket
        try {
            Thread.sleep(10000); // 10 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Task processing sleep interrupted", e);
        }

        Integer result = task.getOriginalValue() * 5;

        task.setResultValue(result);
        task.setStatus(TaskStatus.DONE);

        taskRepository.save(task);

        TaskResponseDTO taskDto = taskMapper.toResponseDTO(task);

        taskStatusUpdateService.sendUpdate(taskDto);

        log.info("Task ID {} processed successfully. Result: {}", taskId, result);
    }
}