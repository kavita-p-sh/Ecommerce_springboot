package Sb_new_project.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    @NotEmpty(message = "Order items required")
    private List<OrderItemRequest> items;

}