package Sb_new_project.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequestDTO {
        @NotBlank(message = "Product name cannot be empty")
        private String name;

        @Size(max = 500, message = "Description too long")
        private String description;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        private Double price;

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer quantity;

  }
