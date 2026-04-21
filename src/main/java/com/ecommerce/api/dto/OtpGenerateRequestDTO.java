package com.ecommerce.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for OTP generation.
 * Accepts email address or phone number as key.
 */
@Data
public class OtpGenerateRequestDTO {

    @NotBlank(message = "Key is required")
    @Size(max = 128, message = "Key must not exceed 128 characters")
    @Pattern(
            regexp = "^(\\S+@\\S+\\.\\S+|\\+?\\d{7,15})$",
            message = "Key must be a valid email or phone number"
    )
    private String key;
}