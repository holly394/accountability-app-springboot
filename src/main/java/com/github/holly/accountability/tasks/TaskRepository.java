package com.github.holly.accountability.tasks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
        FROM Task t
        WHERE t.status in (:statuses)
        AND t.user.id = :userId
        """)
    Page<Task> findByUserId(Long userId, List<TaskStatus> statuses, Pageable pageable);

    @Query("""
        FROM Task t
        WHERE t.user.id in (:userIds)
        """)
    Page<Task> findByUserIdIn(List<Long> userIds, Pageable pageable);


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

    List<Long> userId(Long userId);
}

