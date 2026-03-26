package Sb_new_project.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Role{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 50, message = "Role name must be at most 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String roleName;

    @OneToMany(mappedBy="role")
    @JsonIgnore
    private List<User> users;

}