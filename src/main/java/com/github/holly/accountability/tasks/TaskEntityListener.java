package com.github.holly.accountability.tasks;

import com.github.holly.accountability.config.BeanContextHelper;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import java.time.LocalDateTime;

public class TaskEntityListener {

    @PostPersist
    public void postPersist(Task target) {
        TaskHistoryRepository repository = BeanContextHelper.getBean(TaskHistoryRepository.class);
        var history = new TaskHistory(target.getUser().getId(), target.getId(), target.getDescription(), target.getTimeStart(), target.getTimeEnd(), LocalDateTime.now());
        repository.save(history);
    }

    @PostUpdate
    public void postUpdate(Task target) {
        TaskHistoryRepository repository = BeanContextHelper.getBean(TaskHistoryRepository.class);
        var history = new TaskHistory(target.getUser().getId(), target.getId(), target.getDescription(), target.getTimeStart(), target.getTimeEnd(), LocalDateTime.now());
        repository.save(history);
    }

    @PostRemove
    public void postDelete(Task target) {
    }
}