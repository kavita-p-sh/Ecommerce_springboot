package com.ecommerce.api.service;

import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

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
     * Adds a new product.
     *
     * @param requestDTO the product data to be created
     * @return the created product details
     */
    ProductResponseDTO addProduct(ProductRequestDTO requestDTO);

    /**
     * Fetches all products.
     *
     * @return list of all products
     */
    List<ProductResponseDTO> getAllProducts();

    /**
     * Fetches products based on filters like name, price, or quantity.
     *
     * @param name the product name to filter (optional)
     * @param price the product price to filter (optional)
     * @param quantity the product quantity to filter (optional)
     * @return list of filtered products
     */
    List<ProductResponseDTO> getProducts(String name, BigDecimal price, Integer quantity);


    /**
     * Updates an existing product by its productId
     *
     * @param id the ID of the product to update
     * @param dto the updated product data
     * @return the updated product details
     */
    ProductResponseDTO updateProductById(Long id, ProductUpdateDTO dto);


    void deleteProductById(Long id);
}