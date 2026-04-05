package Sb_new_project.demo.dto;
import Sb_new_project.demo.util.RegexConstant;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class ProductUpdateDTO {

    @Pattern(regexp = RegexConstant.PRODUCT_NAME, message = "Invalid product name")
    private String name;

    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @Positive(message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price can have up to 2 decimal places")
    private Double price;

    @PositiveOrZero(message = "Quantity must be 0 or more")
    private Integer quantity;
}