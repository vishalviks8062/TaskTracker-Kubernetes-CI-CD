package com.tasktracker.backend.dto;

public record CreateTaskRequest(String title, String description, Long bucketId) {
}
