package Sb_new_project.demo.entity;

import Sb_new_project.demo.enums.RoleName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "roles")
@ToString(exclude = "users")
@Data
public class RoleEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleName roleName;

    @OneToMany(mappedBy="role")
    @JsonIgnore
    private List<UserEntity> users;

}