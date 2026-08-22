package com.tasktracker.backend.dto;

import java.time.Instant;

public record TaskDto(Long id, String title, String description, boolean done, Instant createdAt, Long bucketId) {
}
