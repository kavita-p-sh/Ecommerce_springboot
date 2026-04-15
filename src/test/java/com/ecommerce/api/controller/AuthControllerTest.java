package com.ecommerce.api.controller;

import com.ecommerce.api.dto.LoginRequestDTO;
import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.exception.BadRequestException;
import com.ecommerce.api.service.AuthService;
import com.ecommerce.api.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("RamPatel", result.getBody().getUsername());
        verify(authService).register(request);
    }

    /**
     * Tests Register Data is valid or not.
     */
    @Test
    void register_invalidData() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("RamPatel");

        when(authService.register(request))
                .thenThrow(new BadRequestException("Invalid registration data"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authController.register(request)
        );

        assertEquals("Invalid registration data", exception.getMessage());
        verify(authService).register(request);
    }

    /**
     *
     */
    @Test
    void register_DuplicateUser() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("RamPatel");

        when(authService.register(request))
                .thenThrow(new BadRequestException("Username already exists"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authController.register(request)
        );

        assertEquals("Username already exists", exception.getMessage());
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

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("mock-token", result.getBody());


        verify(authService).login(request);

    }

    /**
     * Tests Login Credential is Valid.
     */
    @Test
    void login_invalidCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("RamPatel");

        when(authService.login(request))
                .thenThrow(new BadRequestException("Invalid username or password"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authController.login(request)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(authService).login(request);
    }
    /**
     * Test Logout
     * -HTTP status is (OK)
     * -Logout success message is returned
     */

    @Test
    void logout_success() {
        ResponseEntity<String> result = authController.logout();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(AppConstants.LOGOUT_SUCCESS, result.getBody());
    }


}