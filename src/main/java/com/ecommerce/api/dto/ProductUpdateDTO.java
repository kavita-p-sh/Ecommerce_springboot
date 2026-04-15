package com.ecommerce.api.dto;
import com.ecommerce.api.util.RegexConstant;
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

    @Min(value=0,message = "{product.quantity.min}")
    private Integer quantity;
}