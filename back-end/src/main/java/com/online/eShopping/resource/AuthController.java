package com.online.eShopping.resource;

import com.online.eShopping.dto.JwtResponseDTO;
import com.online.eShopping.dto.UserDTO;
import com.online.eShopping.dto.UserLoginDTO;
import com.online.eShopping.dto.UserRegistrationDTO;
import com.online.eShopping.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        return ResponseEntity.ok(authService.register(registrationDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        return ResponseEntity.ok(authService.login(loginDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        // Eemaldame "Bearer " prefiksi, et saada puhast tokenit
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuth() {
        // Lihtne endpoint autentimise kontrollimiseks
        return ResponseEntity.ok("Authenticated");
    }
}
