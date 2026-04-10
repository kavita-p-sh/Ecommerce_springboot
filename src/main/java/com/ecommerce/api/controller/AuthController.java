package com.ecommerce.api.controller;

import com.ecommerce.api.dto.LoginRequestDTO;
import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.service.AuthService;
import com.ecommerce.api.util.AppConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Handles authentication APIs like login, register, profile and logout.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    /**
     * Register new user.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        log.info("Register request received for username: {}", dto.getUsername());

        UserResponseDTO response = authService.register(dto);

        log.info("User registered successfully: {}", response.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user and return token.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("Login request for username: {}", dto.getUsername());

        String token = authService.login(dto);

        log.info("Login successfully for username: {}", dto.getUsername());

        return ResponseEntity.ok(token);
    }

    /**
     * Logout API for JWT authentication.
     * Since JWT is stateless, server does not store or invalidate tokens.
     * Logout is handled by removing the token on client side.
     * For better security, token blacklist can be used.
     */
    @PostMapping("/logout")
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN, AppConstants.ROLE_MANAGER})
    public ResponseEntity<String> logout() {

        log.info("Logout request received");
        return ResponseEntity.ok(AppConstants.LOGOUT_SUCCESS);
    }
}