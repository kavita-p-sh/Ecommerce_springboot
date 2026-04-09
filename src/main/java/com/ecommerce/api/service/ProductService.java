package com.ecommerce.api.service;

import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;

import java.math.BigDecimal;
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

    /**
     * add new product
     * @param requestDTO
     * @return
     */
    ProductResponseDTO addProduct(ProductRequestDTO requestDTO);


    /**
     * fetch all products
     * @return
     */
    List<ProductResponseDTO> getAllProducts();

    /**
     * fetch products based on filters like name, price or quantity.
     * @param name product name
     * @param price product price
     * @param quantity product quantity
     * @return
     */
    List<ProductResponseDTO> getProducts(String name, BigDecimal price, Integer quantity);

    /**
     * Updates product details using name.
     *
     * @param dto update data
     * @return updated product details
     */
    ProductResponseDTO updateProductByName(ProductUpdateDTO dto);
    /**
     * Deletes product by name.
     *
     * @param name product name
     */
    void deleteProductByName(String name);
}