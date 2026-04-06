package Sb_new_project.demo.dto;

import Sb_new_project.demo.util.RegexConstant;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductRequestDTO {

        @NotBlank(message = "Product name cannot be empty")
        @Pattern(regexp = RegexConstant.PRODUCT_NAME, message = "Invalid product name")
        private String name;

        @NotBlank(message = "Description cannot be blank")
        @Size(min=150, max = 500, message = "Description too long")
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Price can have up to 10 digits and 2 decimal places")
        private BigDecimal price;

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer quantity;

  }
