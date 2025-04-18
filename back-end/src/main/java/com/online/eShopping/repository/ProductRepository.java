package com.online.eShopping.repository;

import com.online.eShopping.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Leia tooted kategooria järgi
    List<Product> findByCategoryId(Long categoryId);

    // Leia tooted kategooria järgi (lehekülgede kaupa)
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Otsi tooteid nime või kirjelduse järgi
    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE LOWER(p.name) " +
            "LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) " +
            "LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

    // Leia tooted hinnawah
}
