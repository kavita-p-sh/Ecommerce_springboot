package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.ProductRequestDTO;
import Sb_new_project.demo.dto.ProductResponseDTO;
import Sb_new_project.demo.dto.ProductUpdateDTO;
import Sb_new_project.demo.service.ProductService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing products.
 * <p>
 * Provides APIs for:
 * - Creating products
 * - Fetching products (with optional filtering)
 * - Updating products
 * - Deleting products
 * </p>
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO dto) {

        log.info("Creating product: {}", dto.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(dto));
    }

    @GetMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    public ResponseEntity<List<ProductResponseDTO>> get(
            @RequestParam(required = false) String name) {

        log.info("Fetching products. Filter: {}", name);

        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(List.of(productService.getProductByName(name)));
        }

        return ResponseEntity.ok(productService.getAllProducts());
    }


    @PutMapping("/{name}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable String name,
            @Valid @RequestBody ProductUpdateDTO dto) {

        log.info("Updating product: {}", name);

        return ResponseEntity.ok(productService.updateProductByName(name, dto));
    }


    @DeleteMapping("/{name}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<String> delete(@PathVariable String name) {

        log.info("Deleting product: {}", name);

        productService.deleteProductByName(name);

        return ResponseEntity.ok(Constant.PRODUCT_DELETED_SUCCESS);
    }
}