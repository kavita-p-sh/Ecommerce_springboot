package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.ProductRequestDTO;
import Sb_new_project.demo.dto.ProductResponseDTO;
import Sb_new_project.demo.dto.ProductUpdateDTO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for managing product operations.
 * Provides methods for:
 * - Creating products
 * - Fetching products
 * - Updating products
 * - Deleting products
 */
public interface ProductService {

    ProductResponseDTO addProduct(ProductRequestDTO requestDTO);

    List<ProductResponseDTO> getAllProducts();


    ProductResponseDTO updateProductByName(ProductUpdateDTO dto);

    void deleteProductByName(String name);
}