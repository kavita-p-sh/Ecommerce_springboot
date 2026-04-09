package com.ecommerce.api.service;

import com.ecommerce.api.dto.LoginRequestDTO;
import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UserResponseDTO;

public interface AuthService {
    /**
     * Authenticates user and returns JWT token.
     *
     * @param dto login request data
     * @return JWT token
     */
    String login(LoginRequestDTO dto);

    /**
     * Registers a new user.
     *
     * @param dto register request data
     * @return user response
     */
    UserResponseDTO register(RegisterRequestDTO dto);
}