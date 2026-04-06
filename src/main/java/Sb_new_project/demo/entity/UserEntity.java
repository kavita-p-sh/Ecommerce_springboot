package Sb_new_project.demo.entity;

import Sb_new_project.demo.util.RegexConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "{username.required}")
    @Pattern(regexp = RegexConstant.USERNAME, message = "{username.valid}")
    @Column(unique = true , nullable = false)
    private String username;

    @NotBlank(message = "{password.required}")
    private String password;

    @NotBlank(message = "{phone.required}")
    @Pattern(regexp = RegexConstant.PHONE, message = "{phone.format}")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotBlank(message = "{email.required}")
    @Pattern(regexp = RegexConstant.EMAIL, message = "{email.valid}")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name="role_id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<OrdersEntity> orders;
}