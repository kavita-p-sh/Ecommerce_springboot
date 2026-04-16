package com.ecommerce.api.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Entity for storing order status details (PLACED, DELIVERED, CANCELLED).
 */
@Entity
@Table(name = "tb_order_status")
@Data
public class OrderStatusEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @Column(nullable = false, unique = true)
    private String statusName;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private List<OrderEntity> orders;
}