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

    @NotBlank
    @Pattern(regexp = RegexConstant.USERNAME, message = "Username must me start with a letter and contains only letter ,numbers,and underScores")
    @Column(unique = true , nullable = false)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexConstant.PHONE, message = "Invalid phone number")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = RegexConstant.EMAIL, message = "Email must be valid")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name="role_id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<OrdersEntity> orders;
}