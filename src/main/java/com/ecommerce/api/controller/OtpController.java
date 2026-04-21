package com.ecommerce.api.controller;

import com.ecommerce.api.dto.OtpGenerateRequestDTO;
import com.ecommerce.api.dto.OtpVerifyRequestDTO;
import com.ecommerce.api.service.OtpService;
import com.ecommerce.api.util.CacheConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * Handles OTP related APIs such as OTP generation and verification.
 */
@Slf4j
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Validated
@Tag(name = "OTP Controller", description = "APIs for OTP generation and verification")
public class OtpController {

    private final OtpService otpService;

    /**
     * Generates a new OTP for the given key if an active OTP does not already exist.
     * Also checks IP-based request limit before generating the OTP.
     */
    @Operation(
            summary = "Generate OTP",
            description = "Generates a new OTP for the given key"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP generated successfully"),
            @ApiResponse(responseCode = "400", description = "OTP already exists"),
            @ApiResponse(responseCode = "429", description = "Too many OTP requests from this IP")
    })
    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@Valid @RequestBody OtpGenerateRequestDTO requestDto,
                                              HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        log.info("OTP generation request received from IP: {}", ip);

        otpService.generateOtp(requestDto.getKey(), ip);
        log.info("OTP generated successfully for IP: {}", ip);

        return ResponseEntity.ok(CacheConstant.OTP_SENT);


    }

    /**
     * Verifies the entered OTP for the given key.
     */
    @Operation(
            summary = "Verify OTP",
            description = "Verifies the OTP entered by the user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "400", description = "expired OTP")
    })
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpVerifyRequestDTO requestDto,
                                            HttpServletRequest request){

        String ip = request.getRemoteAddr();
        log.info("OTP verification request received from IP: {}", ip);

        boolean result = otpService.verifyOtp(requestDto.getKey(), requestDto.getOtp(), ip);

        if (result) {
            log.info("OTP verified successfully from IP: {}", ip);
            return ResponseEntity.ok(CacheConstant.OTP_VERIFIED);
        }

        log.warn("Invalid OTP attempt from IP: {}", ip);
        return ResponseEntity.badRequest().body(CacheConstant.INVALID_OTP);
    }
}