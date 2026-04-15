package com.ecommerce.api.dto;

import com.ecommerce.api.util.RegexConstant;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductRequestDTO {

        @NotBlank(message ="{product.name.required}")
        @Pattern(regexp = RegexConstant.PRODUCT_NAME, message = "{product.name.invalid}")
        private String name;

        @NotBlank(message = "{product.description.required}")
        @Size( max = 500, message = "{product.description.size}")
        private String description;

        @NotNull(message = "{product.price.required}")
        @DecimalMin(value = "0.01", message = "{product.price.min}")
        @Digits(integer = 10, fraction = 2, message = "{product.price.format}")
        private BigDecimal price;

        @NotNull(message = "{product.quantity.required}")
        @Min(value = 0, message = "{product.quantity.min}")
        private Integer quantity;

  }
