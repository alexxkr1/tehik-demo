package com.example.tehik.mappers;

import com.example.tehik.dto.TaskCreationRequestDTO;
import com.example.tehik.dto.TaskResponseDTO;
import com.example.tehik.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ip", ignore = true)
    @Mapping(target = "resultValue", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskCreationRequestDTO dto);

    TaskResponseDTO toResponseDTO(Task task);
}
