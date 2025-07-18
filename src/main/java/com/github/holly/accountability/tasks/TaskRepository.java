package com.github.holly.accountability.tasks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

interface TaskRepository extends JpaRepository<Task, Long> {

}

