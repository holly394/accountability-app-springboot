package com.github.holly.accountability.config.relationships

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "relationships")
class Relationships(
    val partnerId: Integer,
    val status: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //id is generated for you
    var id: Long? = null,
)

