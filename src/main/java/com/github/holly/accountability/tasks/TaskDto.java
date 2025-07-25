package com.github.holly.accountability.tasks;

import java.time.Duration;

public class TaskDto {
    private Long id;

    private String description;

    private TaskStatus status = TaskStatus.PENDING;

    private Duration duration = Duration.ZERO;

    private String durationString;

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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
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

    public Duration getDuration() { return this.duration; }

    public String getDurationString() { return this.durationString; }

}
