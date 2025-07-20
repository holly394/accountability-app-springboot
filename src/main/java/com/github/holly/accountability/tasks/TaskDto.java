package com.github.holly.accountability.tasks;

import com.github.holly.accountability.user.User;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDto {
    private Long id;

    private String description;

    private TaskStatus status = TaskStatus.PENDING;

    private Duration duration = Duration.ZERO;

    // for Hibernate
    public TaskDto() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setTimeDuration(Duration duration) {
        this.duration = duration;
    }

    public Long getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public Duration getTimeDuration() { return this.duration; }

}
