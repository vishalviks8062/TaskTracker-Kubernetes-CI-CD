package com.tasktracker.backend.dto;

// Every field is nullable and means "leave unchanged" when absent — this
// is a partial update (PATCH semantics on a PUT route), not a full
// replace. TaskController only applies fields that are non-null.
public record UpdateTaskRequest(String title, String description, Boolean done, Long bucketId) {
}
