package com.example.tehik.TaskUnitTests;

import com.example.tehik.entity.Task;
import com.example.tehik.enums.TaskStatus;
import com.example.tehik.messaging.TaskMessageConsumer;
import com.example.tehik.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Message listener tests")
public class TaskMessageListenerTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskMessageConsumer consumer;

    private Long taskId;
    private Integer originalValue;
    private Integer expectedResult;
    private Task pendingTask;

    @BeforeEach
    void setup() {
        taskId = 99L;
        originalValue = 10;
        expectedResult = originalValue * 5;

        pendingTask = new Task();
        pendingTask.setId(taskId);
        pendingTask.setOriginalValue(originalValue);
        pendingTask.setStatus(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Should calculate result, update status to DONE, and save task")
    void receiveMessage_ProcessingSuccess() {

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(pendingTask));

        consumer.receiveMessage(taskId);

        verify(taskRepository).save(argThat(task ->
                task.getId().equals(taskId) &&
                        task.getResultValue().equals(expectedResult) &&
                        task.getStatus().equals(TaskStatus.DONE)
        ));
    }

    @Test
    @DisplayName("Should throw RuntimeException if task ID is not found in the repository")
    void receiveMessage_TaskNotFound_ThrowsException() {
        Long nonExistentId = 555L;
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                consumer.receiveMessage(nonExistentId)
        );

        assertEquals("Task not found with ID: " + nonExistentId, exception.getMessage());

        verify(taskRepository, never()).save(any());
    }
}
