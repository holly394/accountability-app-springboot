package com.github.holly.accountability.config.relationships

import org.springframework.data.jpa.repository.JpaRepository

interface RelationshipRepository: JpaRepository<Relationships, Long> {

}
