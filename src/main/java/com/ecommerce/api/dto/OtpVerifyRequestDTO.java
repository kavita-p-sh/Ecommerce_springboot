package com.ecommerce.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for OTP verification.
 * Accepts email address or phone number as key and a 6-digit OTP.
 */
@Data
public class OtpVerifyRequestDTO {

    @NotBlank(message = "Key is required")
    @Size(max = 128, message = "Key must not exceed 128 characters")
    @Pattern(
            regexp = "^(\\S+@\\S+\\.\\S+|\\+?\\d{7,15})$",
            message = "Key must be a valid email or phone number"
    )
    private String key;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be a valid 6-digit number")
    private String otp;
}
