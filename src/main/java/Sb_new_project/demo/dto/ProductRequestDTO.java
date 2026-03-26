package Sb_new_project.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequestDTO {



        @NotBlank(message = "Product name cannot be empty")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;

        @Size(max = 500, message = "Description too long")
        private String description;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Invalid price format")
        private Double price;

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer quantity;

  }
