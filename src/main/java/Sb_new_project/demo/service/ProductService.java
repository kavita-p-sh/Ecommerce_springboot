package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.ProductRequestDTO;
import Sb_new_project.demo.dto.ProductResponseDTO;
import Sb_new_project.demo.dto.ProductUpdateDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {
    @Transactional
    ProductResponseDTO addProduct(ProductRequestDTO requestDTO);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductByName(String productName);

    @Transactional
    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO dto);

    @Transactional
    void deleteProductById(Long id);
}
