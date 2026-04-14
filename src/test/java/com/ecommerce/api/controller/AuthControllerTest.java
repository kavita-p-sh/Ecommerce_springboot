package com.ecommerce.api.controller;

import com.ecommerce.api.dto.LoginRequestDTO;
import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.service.AuthService;
import com.ecommerce.api.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthController.
 *
 * This class verifies authentication-related APIs:
 * - User registration
 * - User login
 * - User logout
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest{

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    /**
     * Tests successful user registration.
     * - HTTP status is 201 (Created)
     * - Returned username matches expected value
     */
    @Test
    void  register_success(){
        RegisterRequestDTO request=new RegisterRequestDTO();
        request.setUsername("RamPatel");

        UserResponseDTO response = new UserResponseDTO();
        response.setUsername("RamPatel");

        when(authService.register(request)).thenReturn(response);

        ResponseEntity<UserResponseDTO> result = authController.register(request);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals("RamPatel", result.getBody().getUsername());
        verify(authService).register(request);
    }

    /**
     * Successfully login
     * HTTP status is 200 ok
     * JWT token is returned
     */
    @Test
    void login_success()
    {
        LoginRequestDTO request= new LoginRequestDTO();
        request.setUsername("Ram Patel");

        when(authService.login(request)).thenReturn("mock-token");

        ResponseEntity<String> result = authController.login(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("mock-token", result.getBody());


        verify(authService).login(request);

    }

    /**
     * Test Logout
     * -HTTP status is 200 (OK)
     * -Logout success message is returned
     */

    @Test
    void logout_success() {
        ResponseEntity<String> result = authController.logout();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(AppConstants.LOGOUT_SUCCESS, result.getBody());
    }


}