package com.github.holly.accountability.relationships;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    @NotNull Relationship getById(@NotNull Long id);

    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        AND r.partner.id =:partnerId
        """)
    Relationship findRelationship(Long userId, Long partnerId);

    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        OR r.partner.id =:userId
        """)
    List<Relationship> getAllByUserId(Long userId);

    @Query("""
        FROM Relationship r
        WHERE (r.user.id =:userId OR r.partner.id =:userId)
        AND r.status = com.github.holly.accountability.relationships.RelationshipStatus.APPROVED
        """)
    List<Relationship> getApprovedRelationshipsByUserIdBothDirections(Long userId);


}
