package com.github.holly.accountability.config.task;

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tasks")
class Task(
    val userId: Integer,
    val description: String,
    val timeInSeconds: Long,
    val status: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
