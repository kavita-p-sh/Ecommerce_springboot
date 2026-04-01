package Sb_new_project.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "roles")
@ToString(exclude = "users")
@Data
public class Role{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @NotBlank(message = "Role name cannot be blank")
    @Column(nullable = false, unique = true, length = 50)
    private String roleName;

    @OneToMany(mappedBy="role")
    @JsonIgnore

    private List<User> users;

}