package com.online.eShopping.service;

import com.online.eShopping.dto.UserDTO;
import com.online.eShopping.dto.UserRegistrationDTO;
import com.online.eShopping.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    /**
     * Registreerib uue kasutaja.
     *
     * @param registrationDTO Kasutaja registreerimise andmed
     * @return Registreeritud kasutaja andmed
     */
    UserDTO registerUser(UserRegistrationDTO registrationDTO);

    /**
     * Tagastab kasutaja andmed ID järgi.
     *
     * @param id Kasutaja ID
     * @return Kasutaja andmed
     */
    UserDTO getUserById(Long id);

    /**
     * Tagastab kõik kasutajad (administraatori jaoks).
     *
     * @param page Lehekülje number (algab 0-st)
     * @param size Üksuste arv leheküljel
     * @return Kasutajate nimekiri
     */
    Page<UserDTO> getAllUsers(int page, int size);

    /**
     * Uuendab kasutaja andmeid.
     *
     * @param id Kasutaja ID
     * @param userUpdateDTO Uuendatud kasutaja andmed
     * @return Uuendatud kasutaja andmed
     */
    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    /**
     * Tagastab praeguse sisselogitud kasutaja andmed.
     *
     * @return Kasutaja andmed
     */
    UserDTO getCurrentUser();

    /**
     * Aktiveerib kasutaja konto.
     *
     * @param id Kasutaja ID
     * @return Aktiveeritud kasutaja andmed
     */
    UserDTO activateUser(Long id);

    /**
     * Deaktiveerib kasutaja konto.
     *
     * @param id Kasutaja ID
     * @return Deaktiveeritud kasutaja andmed
     */
    UserDTO deactivateUser(Long id);

    /**
     * Kontrollib, kas email on juba kasutuses.
     *
     * @param email Kontrollitav email
     * @return true, kui email on juba kasutuses; false, kui ei ole
     */
    boolean existsByEmail(String email);

}
