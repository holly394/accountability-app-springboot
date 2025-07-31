package com.github.holly.accountability.relationships;

import com.github.holly.accountability.tasks.Task;
import com.github.holly.accountability.user.AccountabilitySessionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        OR r.partner.id =:userId
        ORDER BY r.user.id
        """)
    List<Relationship> getAllByUserId(Long userId);

    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        OR r.partner.id =:userId
        AND r.status =:status
        """)
    List<Relationship> getByUserIdAndStatus(Long userId, RelationshipStatus status);
}
