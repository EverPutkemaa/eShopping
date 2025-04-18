package com.online.eShopping.service;

import com.online.eShopping.dto.JwtResponseDTO;
import com.online.eShopping.dto.UserDTO;
import com.online.eShopping.dto.UserLoginDTO;
import com.online.eShopping.dto.UserRegistrationDTO;

public interface AuthService {

    /**
     * Registreerib uue kasutaja.
     *
     * @param registrationDTO Kasutaja registreerimise andmed
     * @return Registreeritud kasutaja andmed
     */
    UserDTO register(UserRegistrationDTO registrationDTO);

    /**
     * Logib kasutaja sisse.
     *
     * @param loginDTO Kasutaja sisselogimise andmed
     * @return JWT vastus, mis sisaldab tokenit ja kasutaja andmeid
     */
    JwtResponseDTO login(UserLoginDTO loginDTO);

    /**
     * Väljastab JWT tokeni uuendatud versiooni (refresh token).
     *
     * @param refreshToken Olemasolev refresh token
     * @return Uuendatud JWT vastus
     */
    JwtResponseDTO refreshToken(String refreshToken);

    /**
     * Kontrollib, kas token on kehtiv.
     *
     * @param token JWT token
     * @return true, kui token on kehtiv; false, kui ei ole
     */
    boolean validateToken(String token);

    /**
     * Logib kasutaja välja.
     *
     * @param token JWT token
     */
    void logout(String token);

}
