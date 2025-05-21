package com.github.holly.accountability.config.relationships;

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "relationships")
class Relationships(
    val user1Id: Integer,
    val user2Id: Integer,
    val status: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)

