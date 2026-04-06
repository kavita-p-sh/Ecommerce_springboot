package Sb_new_project.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class OrdersEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotNull
    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "{order.amount.min}")
    @Digits(integer = 10, fraction = 2, message = "{order.amount.format}")
    private BigDecimal totalAmount;

    @Positive(message = "{order.quantity.positive}")
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