package Sb_new_project.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProductResponseDTO implements Serializable {

    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private LocalDateTime createdDate;
    private String updatedBy;
}