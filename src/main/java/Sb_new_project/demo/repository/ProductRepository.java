package Sb_new_project.demo.repository;

import Sb_new_project.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByName(String name);

    boolean existsByName(String name);
}