package com.github.holly.accountability.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findByUserIdOrderByPurchaseTimeDesc(Long userId, Pageable pageable);
}
