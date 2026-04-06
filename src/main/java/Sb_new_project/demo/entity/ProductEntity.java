package Sb_new_project.demo.entity;
import Sb_new_project.demo.util.RegexConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product name cannot be blank")
    @Column(nullable = false, length = 150,unique=true)
    @Pattern(regexp = RegexConstant.PRODUCT_NAME, message = "Invalid product name")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2, message = "Price can have up to 10 digits and 2 decimal places")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Positive(message = "Quantity must be 0 or more")
    @Column(nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderItemEntity> items;


}