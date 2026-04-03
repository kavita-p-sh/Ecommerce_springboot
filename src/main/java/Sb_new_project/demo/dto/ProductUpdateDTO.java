package Sb_new_project.demo.dto;


import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class ProductUpdateDTO {

    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    private String name;

    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    @Positive(message = "Quantity must be 0 or more")
    private Integer quantity;
}
