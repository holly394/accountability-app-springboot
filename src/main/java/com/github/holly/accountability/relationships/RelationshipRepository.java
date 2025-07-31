package com.github.holly.accountability.relationships;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        AND r.partner.id =:partnerId
        """)
    Relationship findRelationship(Long userId, Long partnerId);

    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        """)
    List<Relationship> getAllByUserId(Long userId);

    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        AND r.status = com.github.holly.accountability.relationships.RelationshipStatus.PENDING
        """)
    List<Relationship> getPendingByUserId(Long userId);

    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        AND r.status = com.github.holly.accountability.relationships.RelationshipStatus.APPROVED
        """)
    List<Relationship> getApprovedByUserId(Long userId);
}
