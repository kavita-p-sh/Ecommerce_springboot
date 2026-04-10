package com.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity representing order information.
 */

@Entity
@Table(name = "orders")
@Data
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;


    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private Integer totalQuantity;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatusEntity status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> orderItems;


}