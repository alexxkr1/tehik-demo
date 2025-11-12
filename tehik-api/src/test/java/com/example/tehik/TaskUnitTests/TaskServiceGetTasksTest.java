package com.example.tehik.TaskUnitTests;

import com.example.tehik.dto.TaskResponseDTO;
import com.example.tehik.entity.Task;
import com.example.tehik.enums.TaskStatus;
import com.example.tehik.mappers.TaskMapper;
import com.example.tehik.repository.TaskRepository;
import com.example.tehik.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service getTasks tests")
public class TaskServiceGetTasksTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private String testIp;
    private Pageable testPageable;
    private Task mockTask;
    private TaskResponseDTO expectedDto;
    private Page<Task> mockPage;

    private Instant mockCreatedAt;
    private Instant mockUpdatedAt;

    @BeforeEach
    void setup() {
        testIp = "192.168.1.1";
        testPageable = Pageable.ofSize(10).withPage(0);
        mockCreatedAt = Instant.parse("2025-11-11T10:00:00Z");
        mockUpdatedAt = Instant.parse("2025-11-11T10:05:00Z");

        mockTask = new Task();
        mockTask.setId(101L);
        mockTask.setIp(testIp);
        mockTask.setOriginalValue(5);

        expectedDto = new TaskResponseDTO(101L, 5, null, TaskStatus.PENDING, mockCreatedAt, mockUpdatedAt);


        mockPage = new PageImpl<>(List.of(mockTask), testPageable, 1);
    }

    @Test
    @DisplayName("Should retrieve and map tasks successfully for a given IP")
    void getTasksByIp_Success() {
        when(taskMapper.toResponseDTO(any(Task.class))).thenReturn(expectedDto);

        when(taskRepository.findAllByIp(testIp, testPageable)).thenReturn(mockPage);

        Page<TaskResponseDTO> result = taskService.getTasksByIp(testIp, testPageable);

        assertNotNull(result, "The result page should not be null.");
        assertFalse(result.isEmpty(), "The result page should not be empty.");
        assertEquals(1, result.getTotalElements(), "The total number of elements should be 1.");

        TaskResponseDTO returnedTask = result.getContent().getFirst();

        assertEquals(expectedDto.id(), returnedTask.id(), "The returned task ID should match the mock.");
        assertEquals(mockCreatedAt, returnedTask.createdAt(), "The createdAt timestamp must be mapped correctly.");
        assertEquals(mockUpdatedAt, returnedTask.updatedAt(), "The updatedAt timestamp must be mapped correctly.");

        verify(taskRepository, times(1)).findAllByIp(testIp, testPageable);

        verify(taskMapper, times(1)).toResponseDTO(mockTask);
    }

    @Test
    @DisplayName("Should return empty page if no tasks are found for the IP")
    void getTasksByIp_NoTasksFound(){
        Page<Task> emptyPage = new PageImpl<>(Collections.emptyList(), testPageable, 0);

        when(taskRepository.findAllByIp(testIp, testPageable)).thenReturn(emptyPage);

        Page<TaskResponseDTO> result = taskService.getTasksByIp(testIp, testPageable);

        assertTrue(result.isEmpty(), "The result page should be empty.");
        assertEquals(0, result.getTotalElements());

        verify(taskRepository, times(1)).findAllByIp(testIp, testPageable);

        verify(taskMapper, never()).toResponseDTO(any());
    }
}
