package com.tasktracker.backend;

import com.tasktracker.backend.dto.BucketDto;
import com.tasktracker.backend.dto.CreateBucketRequest;
import com.tasktracker.backend.dto.TaskDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BucketController {

    private final BucketRepository bucketRepository;
    private final UserRepository userRepository;

    public BucketController(BucketRepository bucketRepository, UserRepository userRepository) {
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
    }

    // The whole kanban board in one call: every bucket the caller owns,
    // each with its tasks nested inside — avoids an extra round trip per
    // column just to render the UI.
    //
    // @Transactional matters here: bucket.getTasks() is a LAZY collection
    // (see Bucket.java), and with spring.jpa.open-in-view=false the
    // Hibernate session that the repository call opened closes the moment
    // findByOwnerIdOrderByCreatedAtAsc() returns. Without this annotation
    // keeping a session open across the whole method, toDto()'s call to
    // bucket.getTasks() below throws LazyInitializationException the
    // instant a bucket actually has tasks in it.
    @Transactional(readOnly = true)
    @GetMapping("/board")
    public List<BucketDto> board(Authentication auth) {
        User owner = currentUser(auth);
        return bucketRepository.findByOwnerIdOrderByCreatedAtAsc(owner.getId())
                .stream().map(this::toDto).toList();
    }

    @PostMapping("/buckets")
    public ResponseEntity<BucketDto> createBucket(@RequestBody CreateBucketRequest request, Authentication auth) {
        if (request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Bucket bucket = new Bucket();
        bucket.setName(request.name());
        bucket.setOwner(currentUser(auth));
        Bucket saved = bucketRepository.save(bucket);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @DeleteMapping("/buckets/{id}")
    public ResponseEntity<Void> deleteBucket(@PathVariable Long id, Authentication auth) {
        User owner = currentUser(auth);
        return bucketRepository.findByIdAndOwnerId(id, owner.getId())
                .map(bucket -> {
                    bucketRepository.delete(bucket); // cascades to delete its tasks too
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + auth.getName()));
    }

    private BucketDto toDto(Bucket bucket) {
        List<TaskDto> tasks = bucket.getTasks().stream()
                .map(t -> new TaskDto(t.getId(), t.getTitle(), t.getDescription(), t.isDone(), t.getCreatedAt(), bucket.getId()))
                .toList();
        return new BucketDto(bucket.getId(), bucket.getName(), tasks);
    }
}
