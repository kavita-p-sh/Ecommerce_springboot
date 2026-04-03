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

    @PostMapping
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO dto) {

        log.info("create Product ", dto.getName());
        ProductResponseDTO response = productService.addProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(String name) {

        log.info("Fetching All Product ", name);
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{name}")
    @RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_USER})
    public ResponseEntity<ProductResponseDTO> getProductByName(@PathVariable String name) {
        log.info("Fetching product by name");
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PutMapping("/{name}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable String name, @Valid @RequestBody ProductUpdateDTO dto) {
        log.info("Update product: ", name);
        ProductResponseDTO response = productService.updateProductByName(name, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{name}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<String> deleteProduct(@PathVariable String name) {
        log.info("Delete Product ", name);
        productService.deleteProductByName(name);
        return ResponseEntity.ok(Constant.PRODUCT_DELETED_SUCCESS);
    }
}