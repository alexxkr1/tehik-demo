package com.example.tehik.TaskUnitTests;

import com.example.tehik.dto.TaskCreationRequestDTO;
import com.example.tehik.dto.TaskResponseDTO;
import com.example.tehik.entity.Task;
import com.example.tehik.enums.TaskStatus;
import com.example.tehik.mappers.TaskMapper;
import com.example.tehik.messaging.TaskMessageProducer;
import com.example.tehik.repository.TaskRepository;
import com.example.tehik.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service createTasks tests")
public class TaskServiceCreateTaskTest {


    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskMessageProducer messageProducer;

    @InjectMocks
    private TaskService taskService;

    private Long taskId;
    private String testIp;
    private TaskResponseDTO expectedResponse;
    private TaskCreationRequestDTO requestDTO;
    private Instant mockCreatedAt;
    private Instant mockUpdatedAt;
    private Task taskBeforeSave;
    private Task taskAfterSave;
    @BeforeEach
    void setup() {
        taskId = 50L;
        testIp = "192.168.1.1";
        mockCreatedAt = Instant.parse("2025-11-11T10:00:00Z");
        mockUpdatedAt = Instant.parse("2025-11-11T10:05:00Z");

        int originalValue = 20;

        requestDTO = new TaskCreationRequestDTO(originalValue);

        taskBeforeSave = new Task();
        taskBeforeSave.setOriginalValue(originalValue);

        taskAfterSave = new Task();
        taskAfterSave.setId(taskId);
        taskAfterSave.setIp(testIp);
        taskAfterSave.setOriginalValue(originalValue);
        taskAfterSave.setStatus(TaskStatus.PENDING);
        taskAfterSave.setCreatedAt(mockCreatedAt);
        taskAfterSave.setUpdatedAt(mockUpdatedAt);

        expectedResponse = new TaskResponseDTO(
                taskId, originalValue, null, TaskStatus.PENDING, mockCreatedAt, mockUpdatedAt
        );

        when(taskMapper.toEntity(requestDTO)).thenReturn(taskBeforeSave);

        when(taskRepository.save(any(Task.class))).thenReturn(taskAfterSave);

        when(taskMapper.toResponseDTO(taskAfterSave)).thenReturn(expectedResponse);

    }

    @Test
    @DisplayName("Should successfully create, save, and queue a task")
    void createTask_Success() {
        TaskResponseDTO result = taskService.createTask(requestDTO, testIp);

        assertEquals(taskId, result.id(), "The returned task ID must match the saved ID.");

        verify(taskRepository).save(argThat(task ->
                task.getIp().equals(testIp) &&
                        task.getOriginalValue().equals(20)
        ));

        verify(messageProducer, times(1)).sendTaskForProcessing(taskId);
    }
}
