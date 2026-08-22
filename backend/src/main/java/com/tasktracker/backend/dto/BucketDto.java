package com.tasktracker.backend.dto;

import java.util.List;

public record BucketDto(Long id, String name, List<TaskDto> tasks) {
}
