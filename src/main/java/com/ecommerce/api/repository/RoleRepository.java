package com.ecommerce.api.repository;

import com.ecommerce.api.entity.RoleEntity;
import com.ecommerce.api.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRoleName(RoleName roleName);
}