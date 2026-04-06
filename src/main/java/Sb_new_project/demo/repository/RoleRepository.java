package Sb_new_project.demo.repository;


import Sb_new_project.demo.entity.RoleEntity;
import Sb_new_project.demo.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByRoleName(RoleName roleName);
}