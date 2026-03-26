package Sb_new_project.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Positive(message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;

    @Positive(message = "Quantity must be 0 or more")
    @Column(nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderItem> items;


}