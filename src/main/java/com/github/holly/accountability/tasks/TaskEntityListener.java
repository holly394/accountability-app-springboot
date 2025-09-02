package com.github.holly.accountability.tasks;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;


public class TaskEntityListener {

    private static TaskHistoryRepository repository;

    @Autowired
    public void setRepository(TaskHistoryRepository repository) {
        TaskEntityListener.repository = repository;
    }

    @PostPersist
    public void postPersist(Task target) {
        var history = new TaskHistory(target.getUser().getId(), target.getId(), target.getDescription(), target.getTimeStart(), target.getTimeEnd(), LocalDateTime.now());
        repository.save(history);
    }

    @PostUpdate
    public void postUpdate(Task target) {
        var history = new TaskHistory(target.getUser().getId(), target.getId(), target.getDescription(), target.getTimeStart(), target.getTimeEnd(), LocalDateTime.now());
        repository.save(history);
    }

    @PostRemove
    public void postDelete(Task target) {
    }
}