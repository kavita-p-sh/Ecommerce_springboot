package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.LoginRequestDTO;
import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.mapper.UserMapper;
import com.ecommerce.api.security.JwtUtil;
import com.ecommerce.api.service.AuthService;
import com.ecommerce.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Handles login and registration functionality.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;


    /**
     * Authenticates the user using username and password
     * and generates a JWT token if credentials are valid.
     *
     * @param dto contains username and password
     * @return JWT token as String
     */
    @Override
    public String login(LoginRequestDTO dto) {

        log.info("Login request for user: {}" , dto.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsername());
        return jwtUtil.generateToken(userDetails);

    }

    /**
     * Registers a new user in the system and returns user details.
     * @param dto contains user registration details
     * @return UserResponseDTO with saved user information
     */
    @Override
    public UserResponseDTO register(RegisterRequestDTO dto) {

        log.info("Register user: {}", dto.getUsername());

        return userService.registerUser(dto);

    }
}