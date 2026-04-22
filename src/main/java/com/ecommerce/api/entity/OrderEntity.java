package com.ecommerce.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing order information.
 */

@Entity
@Table(name = "tb_orders")
@Data
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "order_id", length = 36)
    private UUID orderId;


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