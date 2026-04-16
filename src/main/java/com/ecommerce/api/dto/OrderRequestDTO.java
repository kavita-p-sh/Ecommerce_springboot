package com.ecommerce.api.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotEmpty(message = "{order.items.required}")
    private List<OrderItemRequestDTO> items;
}