package com.github.holly.accountability.relationships;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    @Query("""
        FROM Relationship r
        WHERE r.user.id =:userId
        AND r.partner.id =:partnerId
        """)
    Relationship findRelationship(Long userId, Long partnerId);


    @Query("""
        FROM Relationship r
        WHERE (r.user.id = :userId)
        AND r.status in (:statuses)
        """)
    Page<Relationship> getRelationshipsByUserIdAndStatus(Long userId,
                                                         List<RelationshipStatus> statuses,
                                                         Pageable pageable);

    @Query("""
        FROM Relationship r
        WHERE (r.partner.id = :userId)
        AND r.status in (:statuses)
        """)
    Page<Relationship> getRelationshipsByPartnerIdAndStatus(Long userId,
                                                            List<RelationshipStatus> statuses,
                                                            Pageable pageable);

    @Query("""
        FROM Relationship r
        WHERE (r.user.id = :userId OR r.partner.id =:userId)
        AND r.status in (:statuses)
        """)
    Page<Relationship> getRelationshipsByUserIdAndStatusIgnoreDirection(Long userId,
                                                                        List<RelationshipStatus> statuses,
                                                                        Pageable pageable);

    @Query("""
        FROM Relationship r
        WHERE (r.user.id = :userId AND r.partner.id =:partnerId)
        OR (r.user.id = :partnerId AND r.partner.id =:userId)
        AND r.status in (:statuses)
        """)
    List<Relationship> checkRelationshipExistsByStatusIgnoreDirection(Long userId,
                                                                      Long partnerId,
                                                                      List<RelationshipStatus> statuses);
}
