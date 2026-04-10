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
     * Updates product details using product name.
     *
     * @param dto the product update data
     * @return updated product details
     */
    ProductResponseDTO updateProductByName(ProductUpdateDTO dto);
    /**
     * Deletes a product by name.
     *
     * @param name the product name to delete
     */
    void deleteProductByName(String name);
}