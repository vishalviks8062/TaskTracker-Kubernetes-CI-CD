package com.tasktracker.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BucketRepository extends JpaRepository<Bucket, Long> {
    List<Bucket> findByOwnerIdOrderByCreatedAtAsc(Long ownerId);

    // Scoping every lookup by ownerId, not just id, is what stops user A
    // from reading/deleting/moving tasks into user B's bucket by guessing
    // an id — the query itself returns empty instead of relying on an
    // if-check elsewhere.
    Optional<Bucket> findByIdAndOwnerId(Long id, Long ownerId);
}
