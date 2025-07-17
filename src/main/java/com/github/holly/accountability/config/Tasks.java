package com.github.holly.accountability.config;

import jakarta.persistence.*;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@Entity
@Table(name = "tasks")

class Task {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private String status;
    private Long timeInSeconds;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private Long userId;

    public Task(Long userId, String description){
        this.userId = userId;
        this.description = description;
        this.status = "PENDING";
        this.timeInSeconds = 0L;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setTimeInSeconds(Long time){
        this.timeInSeconds = time;
    }

    public Long getId(){
        return this.id;
    }

    public Long getUserId(){
        return this.userId;
    }

    public String getDescription(){
        return this.description;
    }

    public String getStatus(){
        return this.status;
    }

    public Long getTimeInSeconds(){
        return this.timeInSeconds;
    }
}

interface TaskRepository extends Repository<Task, Long> {

    Task save(Task taskSample);
    Optional<Task> findById(long id);
}

