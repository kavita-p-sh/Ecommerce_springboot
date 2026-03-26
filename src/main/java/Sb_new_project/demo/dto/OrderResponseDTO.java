package Sb_new_project.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {

    private Long orderId;
    private Double totalAmount;
    private Integer totalQuantity;
    private String status;
    private LocalDateTime createdDate;
}