package Sb_new_project.demo.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "order_status")
@Data
public class OrderStatus  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @NotBlank(message = "Status name cannot be blank")
    @Column(nullable = false, unique = true)
    private String statusName;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private List<Orders> orders;
}