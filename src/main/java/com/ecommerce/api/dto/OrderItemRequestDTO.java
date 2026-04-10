package com.ecommerce.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemRequestDTO {


    @NotNull(message = "{Product id is required}")
    private Long productId;

    @NotNull(message = "{orderItem.quantity.required}")
    @Positive(message = "{orderItem.quantity.positive}")
    private Integer quantity;
}
