package Sb_new_project.demo.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateDTO {

    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
