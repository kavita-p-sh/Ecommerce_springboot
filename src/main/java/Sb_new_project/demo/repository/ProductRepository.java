package Sb_new_project.demo.repository;

import Sb_new_project.demo.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {
    Optional<ProductEntity> findByName(String name);

    List<ProductEntity> findByPrice(BigDecimal price);

    List<ProductEntity> findByQuantity(Integer quantity);

    boolean existsByName(String name);
}