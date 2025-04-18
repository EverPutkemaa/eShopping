package com.online.eShopping.repository;

import com.online.eShopping.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Leia ostukorvi ühikud ostukorvi järgi
    List<CartItem> findByCartId(Long cartId);

    // Leia ostukorvi ühik ostukorvi ja toote järgi
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    // Kustuta ostukorvi ühikud ostukorvi järgi
    void deleteByCartId(Long cartId);

}
