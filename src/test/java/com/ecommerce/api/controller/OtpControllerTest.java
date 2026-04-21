package com.ecommerce.api.controller;

import com.ecommerce.api.dto.OtpGenerateRequestDTO;
import com.ecommerce.api.dto.OtpVerifyRequestDTO;
import com.ecommerce.api.service.OtpService;
import com.ecommerce.api.util.CacheConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit test class for  OtpController.
 *
 * This class tests OTP generation and verification APIs.
 * It verifies controller behavior, response status, and interaction with OtpService.
 */
class OtpControllerTest {

    @Mock
    private OtpService otpService;

    @Mock
    private HttpServletRequest httpServletRequest;

    private OtpController otpController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        otpController = new OtpController(otpService);
    }


    /**
     * Test case: OTP should be generated successfully.
     *
     * Verifies:
     * - Correct IP is extracted from request
     * - Service method is called with correct parameters
     * - Response status is  OK
     * - Response body contains success message
     */
    @Test
    void shouldGenerateOtpSuccessfully() {
        OtpGenerateRequestDTO requestDto = new OtpGenerateRequestDTO();
        requestDto.setKey("test135@gmail.com");

        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        when(otpService.generateOtp(anyString(), anyString()))
                .thenReturn(CacheConstant.OTP_SENT);

        ResponseEntity<String> response = otpController.generateOtp(requestDto, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CacheConstant.OTP_SENT, response.getBody());
        verify(otpService).generateOtp("test135@gmail.com", "127.0.0.1");
    }

    /**
     * Test case: OTP verification should succeed when OTP is valid.
     *
     * Verifies:
     * - Correct IP is extracted
     * - Service returns true for valid OTP
     * - Response status is OK
     * - Success message is returned
     */
    @Test
    void shouldVerifyOtpSuccessfully() {
        OtpVerifyRequestDTO requestDto = new OtpVerifyRequestDTO();
        requestDto.setKey("test135@gmail.com");
        requestDto.setOtp("123456");

        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(otpService.verifyOtp("test135@gmail.com", "123456", "127.0.0.1"))
                .thenReturn(true);

        ResponseEntity<String> response = otpController.verifyOtp(requestDto, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CacheConstant.OTP_VERIFIED, response.getBody());
        verify(otpService).verifyOtp("test135@gmail.com", "123456", "127.0.0.1");
    }

    /**
     * Test case: OTP verification should fail when OTP is invalid.
     *
     * - Service returns false for invalid OTP
     * - Response status is 400 BAD REQUEST
     * - Error message is returned
     */
    @Test
    void shouldReturnBadRequestForInvalidOtp() {
        OtpVerifyRequestDTO requestDto = new OtpVerifyRequestDTO();
        requestDto.setKey("test135@gmail.com");
        requestDto.setOtp("999999");

        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(otpService.verifyOtp("test135@gmail.com", "999999", "127.0.0.1"))
                .thenReturn(false);

        ResponseEntity<String> response = otpController.verifyOtp(requestDto, httpServletRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(CacheConstant.INVALID_OTP, response.getBody());
        verify(otpService).verifyOtp("test135@gmail.com", "999999", "127.0.0.1");
    }
}