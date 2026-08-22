package com.tasktracker.backend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Reaches through the bucket relationship to enforce ownership in the
    // query itself: a task only comes back if it lives in a bucket owned
    // by this user.
    Optional<Task> findByIdAndBucket_Owner_Id(Long id, Long ownerId);
}
