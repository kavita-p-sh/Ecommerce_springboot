package com.ecommerce.api.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequestDTO> items;
}