package com.online.eShopping.repository;

import com.online.eShopping.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Leia tellimused kasutaja järgi
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // Leia tellimused staatuse järgi
    Page<Order> findByStatus(String status, Pageable pageable);

    // Leia tellimused ajavahemiku järgi
    Page<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Leia tellimused kasutaja ja staatuse järgi
    Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}
