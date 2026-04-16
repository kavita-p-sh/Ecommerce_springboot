package com.ecommerce.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Entity representing user table in database.
 */

@Data
@Entity
@Table(name = "tb_users")

public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true , nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(nullable = false, name="role_id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    @JsonIgnore
    private List<OrderEntity> orders;


}
