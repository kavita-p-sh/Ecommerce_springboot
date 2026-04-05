package Sb_new_project.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderResponseDTO implements Serializable {

    private Long orderId;
    private Double  totalAmount;
    private Integer totalQuantity;
    private String status;
    private LocalDateTime createdTimestamp;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedTimeStamp;

}