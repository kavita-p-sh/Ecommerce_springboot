package com.ecommerce.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDTO implements Serializable {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String createdBy;
    private LocalDateTime createdTimestamp;
    private String updatedBy;
    private LocalDateTime updatedTimestamp;
}