package com.example.tehik.controller;

import com.example.tehik.dto.TaskCreationRequestDTO;
import com.example.tehik.dto.TaskResponseDTO;
import com.example.tehik.service.TaskService;
import com.example.tehik.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.example.tehik.utils.RequestUtil.getClientIpAddress;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getTasks(
                                           HttpServletRequest request,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size,
                                           @RequestParam(value = "sort", defaultValue = "desc") String sort
    ) {
        String clientIp = getClientIpAddress(request);
        Sort.Direction sortDirection = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "createdAt")
        );
        Page<TaskResponseDTO> pagedTasks = taskService.getTasksByIp(clientIp, pageable);

        return ResponseEntity.ok(pagedTasks);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskCreationRequestDTO requestDTO,
            HttpServletRequest request) {

        String clientIp = RequestUtil.getClientIpAddress(request);
        TaskResponseDTO responseDTO = taskService.createTask(requestDTO, clientIp);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }
}
