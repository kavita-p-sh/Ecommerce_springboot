package Sb_new_project.demo.dto;
import Sb_new_project.demo.util.RegexConstant;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {

    @Pattern(regexp = RegexConstant.PRODUCT_NAME, message = "{product.name.invalid}")
    private String name;

    @Size(max = 500, message = "{product.description.size}")
    private String description;

    @DecimalMin(value = "0.01", message = "{product.price.min}")
    @Digits(integer = 10, fraction = 2, message = "{product.price.format}")
    private BigDecimal price;

    @Positive(message = "{product.quantity.positive}")
    private Integer quantity;
}