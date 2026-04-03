package Sb_new_project.demo.entity;

import Sb_new_project.demo.util.RegexConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Pattern(regexp = RegexConstant.USERNAME, message = "Username must me start with a letter and contains only letter ,numbers,and underScores")
    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = RegexConstant.PASSWORD, message = "Password must contain at least 6 characters, one letter, one number and one special character")
    @Size(min = 6, max = 20, message = "Password must be at least 6 characters")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexConstant.PHONE, message = "Invalid phone number")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;


    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = RegexConstant.EMAIL, message = "Email must be valid")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Orders> orders;
}