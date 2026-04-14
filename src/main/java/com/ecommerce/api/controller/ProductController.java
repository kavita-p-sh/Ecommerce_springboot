package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;
import com.ecommerce.api.service.ProductService;
import com.ecommerce.api.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * - Creating products
 * - Fetching products
 * - Updating products
 * - Deleting products
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;

    /**
     * create product
     * @param dto request data for product creation
     * @return ResponseEntity containing created product details with HTTP status 201 (Created)
     */
    @PostMapping
    @RolesAllowed(AppConstants.ROLE_ADMIN)
    @Operation(summary = "Create Product",
                description = "Creates a new Product. Only admin can create products.")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO dto) {

        log.info("creating Product {}", dto.getName());
        ProductResponseDTO response = productService.addProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Fetches all products or filters
     * by name,
     * price, or
     * quantity using query parameters.
     */

    @GetMapping
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN, AppConstants.ROLE_MANAGER})
    @Operation(summary = "Get products",
               description = "Fetch all products and filter them by name,price and quantity.")
    public ResponseEntity<List<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer quantity) {

        log.info("Fetching products with filters: name={}, price={}, quantity={}", name, price, quantity);

        return ResponseEntity.ok(productService.getProducts(name, price, quantity));
    }

    /**
     * Updates an existing product by its ID.
     *
     * @param id product ID
     * @param dto updated product data
     * @return updated product details
     */
    @PutMapping("/{id}")
    @RolesAllowed(AppConstants.ROLE_ADMIN)
    @Operation(summary = "Update Product By ID",
               description ="Update product details using product ID.Only Admin can update products" )
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO dto) {

        log.info("Updating product with id: {}", id);
        ProductResponseDTO response = productService.updateProductById(id, dto);
        log.info("Product updated successfully: {}", response.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return success message after product is deleted
     */
    @DeleteMapping("/{id}")
    @RolesAllowed(AppConstants.ROLE_ADMIN)
    @Operation(summary = "Delete product by ID",
               description = "Deletes a product by ID .Only admin can delete product")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {

        log.info("Delete Product with id: {}", id);

        productService.deleteProductById(id);

        log.info("Product deleted successfully with id: {}", id);

        return ResponseEntity.ok(AppConstants.PRODUCT_DELETED_SUCCESS);
    }
}