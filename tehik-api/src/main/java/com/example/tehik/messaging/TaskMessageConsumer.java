package com.example.tehik.messaging;

import com.example.tehik.entity.Task;
import com.example.tehik.enums.TaskStatus;
import com.example.tehik.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskMessageConsumer {

    private final TaskRepository taskRepository;

    @RabbitListener(queues = {"${app.rabbitmq.queue}"})
    public void receiveMessage(Long taskId) {
        log.info("Received task ID: {}. Starting processing.", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        Integer result = task.getOriginalValue() * 5;

        task.setResultValue(result);
        task.setStatus(TaskStatus.DONE);

        taskRepository.save(task);

        log.info("Task ID {} processed successfully. Result: {}", taskId, result);
    }
}