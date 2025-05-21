package com.github.holly.accountability.config.task

import com.github.holly.accountability.config.task.Task
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface TaskRepository: JpaRepository<Task, Long> {

    fun findByTaskId(id: BigInteger): Task?
}
