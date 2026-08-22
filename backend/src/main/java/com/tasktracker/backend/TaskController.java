package com.tasktracker.backend;

import com.tasktracker.backend.dto.CreateTaskRequest;
import com.tasktracker.backend.dto.TaskDto;
import com.tasktracker.backend.dto.UpdateTaskRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final BucketRepository bucketRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, BucketRepository bucketRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody CreateTaskRequest request, Authentication auth) {
        if (request.title() == null || request.title().isBlank() || request.bucketId() == null) {
            return ResponseEntity.badRequest().build();
        }
        User owner = currentUser(auth);
        // Looking the bucket up scoped to this owner is what stops someone
        // from filing a task into a bucket that belongs to a different user.
        Bucket bucket = bucketRepository.findByIdAndOwnerId(request.bucketId(), owner.getId()).orElse(null);
        if (bucket == null) {
            return ResponseEntity.badRequest().build();
        }

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setBucket(bucket);
        Task saved = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest request, Authentication auth) {
        User owner = currentUser(auth);
        Task task = taskRepository.findByIdAndBucket_Owner_Id(id, owner.getId()).orElse(null);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.done() != null) {
            task.setDone(request.done());
        }
        if (request.bucketId() != null) {
            // Moving a task to a different bucket (the dropdown in the UI) —
            // still scoped to this owner, so you can't move a task into
            // someone else's board either.
            Bucket newBucket = bucketRepository.findByIdAndOwnerId(request.bucketId(), owner.getId()).orElse(null);
            if (newBucket == null) {
                return ResponseEntity.badRequest().build();
            }
            task.setBucket(newBucket);
        }

        return ResponseEntity.ok(toDto(taskRepository.save(task)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication auth) {
        User owner = currentUser(auth);
        return taskRepository.findByIdAndBucket_Owner_Id(id, owner.getId())
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + auth.getName()));
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.isDone(), task.getCreatedAt(), task.getBucket().getId());
    }
}
