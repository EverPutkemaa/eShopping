package com.online.eShopping.repository;

import com.online.eShopping.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Leia roll nime järgi
    Optional<Role> findByName(String name);

    // Kontrolli, kas roll eksisteerib
    boolean existsByName(String name);
}
