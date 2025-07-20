package com.github.holly.accountability.tasks;

import com.github.holly.accountability.user.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
}

