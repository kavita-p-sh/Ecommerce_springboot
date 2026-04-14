package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.LoginRequestDTO;
import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.mapper.UserMapper;
import com.ecommerce.api.security.JwtUtil;
import com.ecommerce.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test class for AuthServiceImpl.
 * This class tests the authentication service methods such as:
 * - user login
 * - user registration
 */
@ExtendWith(MockitoExtension.class)
public class AuthSeviceImplTest {

    /**
     * Mocked AuthenticationManager.
     */
    @Mock
    private AuthenticationManager authenticationManager;

    /**
     * Mocked UserDetailsService used to load user details by username.
     */
    @Mock
    private UserDetailsService userDetailsService;

    /**
     * Mocked JwtUtil used to generate JWT token after successful login.
     */
    @Mock
    private JwtUtil jwtUtil;

    /**
     * Mocked UserService used to register a new user.
     */
    @Mock
    private UserService userService;

    /**
     * Injects all mocked dependencies into AuthServiceImpl.
     */
    @InjectMocks
    private AuthServiceImpl authService;


    /**
     * Tests successful login.
     */
    @Test
    void login_success() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("ram");
        dto.setPassword("ram@123");

        UserDetails userDetails = User.withUsername("ram")
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername("ram")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mock-jwt-token");

        String result = authService.login(dto);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("ram", "ram@123")
        );
        verify(userDetailsService).loadUserByUsername("ram");
        verify(jwtUtil).generateToken(userDetails);

        assertEquals("mock-jwt-token", result);
    }

    /**
     * Tests successful user registration.
     */
    @Test
    void register_success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("ram");
        dto.setEmail("ram@gmail.com");
        dto.setPassword("ram@123");
        dto.setPhoneNumber("9876543210");

        UserResponseDTO response = new UserResponseDTO();
        response.setUsername("ram");
        response.setEmail("ram@gmail.com");
        response.setPhoneNumber("9876543210");

        when(userService.registerUser(dto)).thenReturn(response);

        UserResponseDTO result = authService.register(dto);

        verify(userService).registerUser(dto);

        assertEquals("ram", result.getUsername());
        assertEquals("ram@gmail.com", result.getEmail());
        assertEquals("9876543210", result.getPhoneNumber());
    }
}


