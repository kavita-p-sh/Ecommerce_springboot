package com.ecommerce.api.entity;

import com.ecommerce.api.util.RegexConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Entity representing user table in database.
 */

@Data
@Entity
@Table(name = "users")

public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "{username.required}")
    @Pattern(regexp = RegexConstant.USERNAME, message = "{username.valid}")
    @Column(unique = true , nullable = false)
    private String username;

    @NotBlank(message = "{password.required}")
    @Column(nullable = false)
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
    @ToString.Exclude
    @JoinColumn(nullable = false, name="role_id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    @JsonIgnore
    private List<OrdersEntity> orders;


}
