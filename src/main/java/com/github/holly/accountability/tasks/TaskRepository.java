package com.github.holly.accountability.tasks;

import com.github.holly.accountability.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);


    @Query("""
        FROM Task t
        WHERE t.status = com.github.holly.accountability.tasks.TaskStatus.COMPLETED
        AND t.user.id =:userId
        """)
    List<Task> findCompleted(Long userId);

    @Query("""
        FROM Task t
        WHERE t.status = com.github.holly.accountability.tasks.TaskStatus.IN_PROGRESS
        AND t.user.id =:userId
        """)
    List<Task> findInProgress(Long userId);

    @Query("""
        FROM Task t
        WHERE t.status = com.github.holly.accountability.tasks.TaskStatus.PENDING
        AND t.user.id =:userId
        """)
    List<Task> findPending(Long userId);

    @Query("""
        FROM Task t
        WHERE t.status = com.github.holly.accountability.tasks.TaskStatus.APPROVED
        AND t.user.id =:userId
        """)
    List<Task> findApproved(Long userId);
}

