package com.ecommerce.api.entity;
import com.ecommerce.api.util.RegexConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity representing product details.
 */
@Entity
@Table(name = "products")
@Data
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "{product.name.required}")
    @Column(nullable = false, length = 150,unique=true)
    @Pattern(regexp = RegexConstant.PRODUCT_NAME, message = "{product.name.invalid}")
    private String name;

    @NotBlank(message = "{product.description.required}")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderItemEntity> items;


}