package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;
import com.ecommerce.api.service.ProductService;
import com.ecommerce.api.util.AppConstants;
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
public class ProductController {

    private final ProductService productService;

    /**
     * create product
     * @param dto request data for product creation
     * @return
     */
    @PostMapping
    @RolesAllowed(AppConstants.ROLE_ADMIN)
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
    public ResponseEntity<List<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer quantity) {

        log.info("Fetching products with filters: name={}, price={}, quantity={}", name, price, quantity);

        return ResponseEntity.ok(productService.getProducts(name, price, quantity));
    }
    /**
     * update product by name
     * @param dto contains updated product data
     * @return update product detail
     */
    @PutMapping
    @RolesAllowed({AppConstants.ROLE_ADMIN})
    public ResponseEntity<ProductResponseDTO> updateProduct(@Valid @RequestBody ProductUpdateDTO dto) {
        log.info("Update product: {}", dto.getName());
        ProductResponseDTO response = productService.updateProductByName(dto );
        log.info("Product update SucessFully by username:{}",response.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete product by name
     * @param name product name to delete
     * @return success message
     */
    @DeleteMapping("/{name}")
    @RolesAllowed(AppConstants.ROLE_ADMIN)
    public ResponseEntity<String> deleteProduct(@PathVariable String name) {
        log.info("Delete Product: {}", name);
        productService.deleteProductByName(name);
        log.info("Product deleted successfully: {}", name);
        return ResponseEntity.ok(AppConstants.PRODUCT_DELETED_SUCCESS);
    }
}