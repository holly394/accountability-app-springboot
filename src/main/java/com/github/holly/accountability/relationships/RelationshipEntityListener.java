package com.github.holly.accountability.relationships;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;


public class RelationshipEntityListener {

    private static RelationshipHistoryRepository repository;

    @Autowired
    public void setRepository(RelationshipHistoryRepository repository) {
        RelationshipEntityListener.repository = repository;
    }

    @PostPersist
    public void postPersist(Relationship target) {
        var history = new RelationshipHistory(target.getId(), target.getUser().getId(), target.getPartner().getId(), target.getStatus(), LocalDateTime.now());
        repository.save(history);
    }

    @PostUpdate
    public void postUpdate(Relationship target) {
        var history = new RelationshipHistory(target.getId(), target.getUser().getId(), target.getPartner().getId(), target.getStatus(), LocalDateTime.now());
        repository.save(history);
    }

    @PostRemove
    public void postDelete(Relationship target) {
    }
}