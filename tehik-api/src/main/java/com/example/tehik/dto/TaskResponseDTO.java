package com.example.tehik.dto;

import com.example.tehik.enums.TaskStatus;
import java.time.Instant;

public record TaskResponseDTO(
    Long id,
    Integer originalValue,
    Integer resultValue,
    TaskStatus status,
    Instant createdAt,
    Instant updatedAt
) { }
