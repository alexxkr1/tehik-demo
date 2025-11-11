package com.example.tehik.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TaskCreationRequestDTO(
        @NotNull(message = "Task value must be present")
        @Min(value = 1, message = "Task value must be positive")
        Integer originalValue
) { }
