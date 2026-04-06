package Sb_new_project.demo.dto;
import Sb_new_project.demo.util.RegexConstant;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {

    @Pattern(regexp = RegexConstant.PRODUCT_NAME, message = "Invalid product name")
    private String name;

    @Size(min = 150, max = 500, message = "Description must be between 150 and 500 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price can have up to 2 decimal places")
    private BigDecimal price;

    @Positive(message = "Quantity must me greater than 0")
    private Integer quantity;
}