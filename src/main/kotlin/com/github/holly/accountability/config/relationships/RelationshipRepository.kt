package com.github.holly.accountability.config.relationships

import com.github.holly.accountability.config.relationships.Relationships
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface RelationshipRepository: JpaRepository<Relationships, Long> {

    fun findByRelationshipId(id: BigInteger): Relationships?
}
