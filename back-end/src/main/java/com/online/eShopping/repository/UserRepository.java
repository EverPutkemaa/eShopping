package com.online.eShopping.repository;


import com.online.eShopping.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Leia kasutaja meiliaadressi järgi
    Optional<User> findByEmail(String email);

    // Kontrolli, kas meiliaadress on juba kasutuses
    boolean existsByEmail(String email);

    // Leia kasutajad rolli järgi
    Page<User> findByRolesName(String roleName, Pageable pageable);

    // Leia kasutajad staatuse järgi
    Page<User> findByEnabled(boolean enabled, Pageable pageable);

    // Loenda kasutajaid rolli järgi
    long countByRolesName(String roleName);

}
