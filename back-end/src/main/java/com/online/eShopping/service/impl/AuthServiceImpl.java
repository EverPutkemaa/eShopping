package com.online.eShopping.service.impl;


import com.online.eShopping.config.security.JwtTokenProvider;
import com.online.eShopping.dto.JwtResponseDTO;
import com.online.eShopping.dto.UserDTO;
import com.online.eShopping.dto.UserLoginDTO;
import com.online.eShopping.dto.UserRegistrationDTO;
import com.online.eShopping.exception.ResourceNotFoundException;
import com.online.eShopping.exception.InvalidCredentialsException;
import com.online.eShopping.exception.InvalidTokenException;
import com.online.eShopping.model.User;
import com.online.eShopping.repository.UserRepository;
import com.online.eShopping.service.AuthService;
import com.online.eShopping.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDTO register(UserRegistrationDTO registrationDTO) {
        log.info("Registering new user with email: {}", registrationDTO.getEmail());
        return userService.registerUser(registrationDTO);
    }

    @Override
    @Transactional
    public JwtResponseDTO login(UserLoginDTO loginDTO) {
        log.info("Authenticating user: {}", loginDTO.getEmail());

        try {
            // Autendi kasutaja
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

            // Sea autentimiskontekst
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Loo JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);

            // Leia kasutaja andmed
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userDetails.getUsername()));

            // Loo vastus
            JwtResponseDTO response = JwtResponseDTO.builder()
                    .token(jwt)
                    .type("Bearer")
                    .id(user.getId())
                    .email(user.getEmail())
                    .roles(user.getRoles().stream()
                            .map(role -> role.getName())
                            .collect(Collectors.toSet()))
                    .build();

            log.info("User authenticated successfully: {}", loginDTO.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginDTO.getEmail(), e);
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public JwtResponseDTO refreshToken(String refreshToken) {
        log.info("Refreshing token");

        // Kontrolli, kas token on kehtiv
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Hangi kasutajanimi tokenist
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Leia kasutaja
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));

        // Loo uus token
        String newToken = jwtTokenProvider.generateTokenFromUsername(username);

        // Loo vastus
        JwtResponseDTO response = JwtResponseDTO.builder()
                .token(newToken)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .build();

        log.info("Token refreshed successfully for user: {}", username);
        return response;
    }

    @Override
    public boolean validateToken(String token) {
        log.info("Validating token");
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public void logout(String token) {
        log.info("Logging out user");

        // Puhasta autentimiskontekst
        SecurityContextHolder.clearContext();

        // Lisaks võid lisada tokeni must nimekirja, et vältida selle taaskasutamist
        // Selle implementatsioon sõltub sinu nõuetest
        // jwtTokenProvider.addToBlacklist(token);

        log.info("User logged out successfully");
    }

}
