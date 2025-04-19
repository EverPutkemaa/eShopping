package com.online.eShopping.repository;

import com.online.eShopping.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Leia kategooria nime järgi
    Optional<Category> findByName(String name);

    // Leia alamkategooriad vanemkategooria järgi
    List<Category> findByParentId(Long parentId);

    // Leia peakategooriad (millel pole vanemkategooriat)
    List<Category> findByParentIsNull();

    // Leia kategooriad nime järgi (otsinguga)
    @Query("SELECT c FROM Category c " +
            "WHERE LOWER(c.name) " +
            "LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Category> search(@Param("keyword") String keyword, Pageable pageable);

    // Kontrolli, kas kategooria on olemas antud nimega
    boolean existsByName(String name);

}
