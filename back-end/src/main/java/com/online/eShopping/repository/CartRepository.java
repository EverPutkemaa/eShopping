package com.online.eShopping.repository;


import com.online.eShopping.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Leia ostukorv kasutaja järgi
    Optional<Cart> findByUserId(Long userId);

    // Kustuta ostukorv kasutaja järgi
    void deleteByUserId(Long userId);
}
