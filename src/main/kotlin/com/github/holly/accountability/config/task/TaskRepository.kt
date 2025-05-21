package com.github.holly.accountability.config.task

import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository: JpaRepository<Task, Long> {

}
