package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.ProductRequestDTO;
import Sb_new_project.demo.dto.ProductResponseDTO;
import Sb_new_project.demo.dto.ProductUpdateDTO;

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

    ProductResponseDTO getProductByName(String name);

    ProductResponseDTO updateProductByName(String name, ProductUpdateDTO dto);

    void deleteProductByName(String name);
}