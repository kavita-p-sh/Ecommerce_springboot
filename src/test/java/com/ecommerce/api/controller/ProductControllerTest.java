package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;
import com.ecommerce.api.service.ProductService;
import com.ecommerce.api.util.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing for product controller
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest{

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductRequestDTO productRequestDTO;
    private ProductUpdateDTO productUpdateDTO;
    private ProductResponseDTO productResponseDTO;

    /**
     * Initializes test data before each test case.
     */
    @BeforeEach
    void setProducts() {
        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Laptop");
        productRequestDTO.setDescription("Dell Laptop");
        productRequestDTO.setPrice(new BigDecimal("55000"));
        productRequestDTO.setQuantity(10);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setProductId(1L);
        productResponseDTO.setName("Laptop");
        productResponseDTO.setDescription("Dell Laptop");
        productResponseDTO.setPrice(new BigDecimal("55000"));
        productResponseDTO.setQuantity(10);

        productUpdateDTO = new ProductUpdateDTO();
        productUpdateDTO.setName("Updated Laptop");
        productUpdateDTO.setDescription("Updated Dell Laptop");
        productUpdateDTO.setPrice(new BigDecimal("60000"));
        productUpdateDTO.setQuantity(8);

    }

    /**
     * Tests product creation successfully
     */

    @Test
    void createProduct_success() {
        when(productService.addProduct(productRequestDTO)).thenReturn(productResponseDTO);

        ResponseEntity<ProductResponseDTO> response = productController.createProduct(productRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getProductId());
        assertEquals("Laptop", response.getBody().getName());
        assertEquals("Dell Laptop", response.getBody().getDescription());
        assertEquals(new BigDecimal("55000"), response.getBody().getPrice());
        assertEquals(10, response.getBody().getQuantity());

        verify(productService).addProduct(productRequestDTO);

    }
    /**
     *  Tests create products failure
     */
    @Test
    void createProduct_failure() {
        when(productService.addProduct(productRequestDTO))
                .thenThrow(new RuntimeException("Product already exists"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productController.createProduct(productRequestDTO)
        );

        assertEquals("Product already exists", exception.getMessage());
        verify(productService).addProduct(productRequestDTO);
    }

    /**
     * Tests fetching products with filters.
     */
    @Test
    void getProducts_success() {
        when(productService.getProducts("Laptop", new BigDecimal("55000"), 10))
                .thenReturn(List.of(productResponseDTO));

        ResponseEntity<List<ProductResponseDTO>> response =
                productController.getProducts("Laptop", new BigDecimal("55000"), 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Laptop", response.getBody().get(0).getName());
        assertEquals(new BigDecimal("55000"), response.getBody().get(0).getPrice());
        assertEquals(10, response.getBody().get(0).getQuantity());

        verify(productService).getProducts("Laptop", new BigDecimal("55000"), 10);


    }

    /**
     * get Products failure
     */
    @Test
    void getProducts_failure() {
        when(productService.getProducts("Laptop", new BigDecimal("55000"), 10))
                .thenThrow(new RuntimeException("Invalid filter"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productController.getProducts("Laptop", new BigDecimal("55000"), 10)
        );

        assertEquals("Invalid filter", exception.getMessage());
        verify(productService).getProducts("Laptop", new BigDecimal("55000"), 10);
    }


    /**
     * Tests updating an existing product.
     */
    @Test
    void updateProduct_success() {
        ProductResponseDTO updatedResponse = new ProductResponseDTO();
        updatedResponse.setProductId(1L);
        updatedResponse.setName("Updated Laptop");
        updatedResponse.setDescription("Updated Dell Laptop");
        updatedResponse.setPrice(new BigDecimal("60000"));
        updatedResponse.setQuantity(8);

        when(productService.updateProductById(1L, productUpdateDTO)).thenReturn(updatedResponse);

        ResponseEntity<ProductResponseDTO> response =
                productController.updateProduct(1L, productUpdateDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getProductId());
        assertEquals("Updated Laptop", response.getBody().getName());
        assertEquals("Updated Dell Laptop", response.getBody().getDescription());
        assertEquals(new BigDecimal("60000"), response.getBody().getPrice());
        assertEquals(8, response.getBody().getQuantity());

        verify(productService).updateProductById(1L, productUpdateDTO);
    }

    /**
     * update product failure
     */
    @Test
    void updateProduct_notFound() {
        when(productService.updateProductById(1L, productUpdateDTO))
                .thenThrow(new RuntimeException("Product not found"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productController.updateProduct(1L, productUpdateDTO)
        );

        assertEquals("Product not found", exception.getMessage());
        verify(productService).updateProductById(1L, productUpdateDTO);
    }
    /**
     * Tests Deleting product by ID
     */

    @Test
    void deleteProduct_success() {
        doNothing().when(productService).deleteProductById(1L);

        ResponseEntity<String> response = productController.deleteProduct(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(AppConstants.PRODUCT_DELETED_SUCCESS, response.getBody());

        verify(productService).deleteProductById(1L);
    }

    /**
     * Delete product not found exception
     */
    @Test
    void deleteProduct_notFound() {
        doThrow(new RuntimeException("Product not found"))
                .when(productService).deleteProductById(1L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productController.deleteProduct(1L)
        );

        assertEquals("Product not found", exception.getMessage());
        verify(productService).deleteProductById(1L);
    }
}











