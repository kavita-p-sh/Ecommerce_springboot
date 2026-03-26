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

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<ProductResponseDTO> addProduct(
            @Valid @RequestBody ProductRequestDTO dto) {

        log.info("Adding product: {}", dto.getName());

        ProductResponseDTO response = productService.addProduct(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.info("Fetching all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/name/{name}")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<ProductResponseDTO> getProductByName(
            @PathVariable String name) {
        log.info("Fetching product with name: {}", name);
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PutMapping("/{id}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {

        log.info("Updating product with id: {}", id);

        return ResponseEntity.ok(
                productService.updateProduct(id, updateDTO)
        );
    }

    @DeleteMapping("/{id}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}