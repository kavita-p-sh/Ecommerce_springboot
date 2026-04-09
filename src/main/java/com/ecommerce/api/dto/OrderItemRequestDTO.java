package com.ecommerce.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemRequestDTO {


    @NotNull(message = "Product id is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
