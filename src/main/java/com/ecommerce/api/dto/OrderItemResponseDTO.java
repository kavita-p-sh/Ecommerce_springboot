package com.ecommerce.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponseDTO {

    private Long orderItemId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}