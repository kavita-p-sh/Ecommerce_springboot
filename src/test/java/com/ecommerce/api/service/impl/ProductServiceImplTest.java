package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;
import com.ecommerce.api.entity.ProductEntity;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for ProductServiceImpl.
 *
 * This class tests:
 * - Adding a new product
 * - Fetching products by name, price, and quantity
 * - Fetching all products
 * - Updating product by ID
 * - Deleting product by ID
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    /**
     * Mocked ProductRepository.
     */
    @Mock
    private ProductRepository productRepository;

    /**
     * Mocked ProductMapper.
     */
    @Mock
    private ProductMapper productMapper;

    /**
     * ProductServiceImpl object with injected mocks.
     */
    @InjectMocks
    private ProductServiceImpl productService;

    /**
     * Tests successful product creation.
     */
    @Test
    void addProduct_success() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Laptop");
        request.setDescription("Dell Laptop");
        request.setPrice(new BigDecimal("55000"));
        request.setQuantity(10);

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setProductId(1L);
        savedProduct.setName("Laptop");
        savedProduct.setDescription("Dell Laptop");
        savedProduct.setPrice(new BigDecimal("55000"));
        savedProduct.setQuantity(10);

        ProductResponseDTO response = new ProductResponseDTO();
        response.setProductId(1L);
        response.setName("Laptop");
        response.setDescription("Dell Laptop");
        response.setPrice(new BigDecimal("55000"));
        response.setQuantity(10);

        when(productRepository.existsByName("Laptop")).thenReturn(false);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(response);

        ProductResponseDTO result = productService.addProduct(request);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(new BigDecimal("55000"), result.getPrice());
        assertEquals(10, result.getQuantity());

        verify(productRepository).existsByName("Laptop");
        verify(productRepository).save(any(ProductEntity.class));
        verify(productMapper).toDTO(savedProduct);
    }

    /**
     * Tests fetching products by name.
     */
    @Test
    void getProducts_byName_success() {
        ProductEntity product = new ProductEntity();
        product.setProductId(1L);
        product.setName("Laptop");

        ProductResponseDTO response = new ProductResponseDTO();
        response.setProductId(1L);
        response.setName("Laptop");

        when(productRepository.findByName("Laptop")).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(response);

        List<ProductResponseDTO> result = productService.getProducts("Laptop", null, null);

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());

        verify(productRepository).findByName("Laptop");
        verify(productMapper).toDTO(product);
    }

    /**
     * Tests fetching products by price.
     */
    @Test
    void getProducts_byPrice_success() {
        BigDecimal price = new BigDecimal("500");

        ProductEntity product = new ProductEntity();
        product.setProductId(1L);
        product.setName("Mouse");
        product.setPrice(price);

        ProductResponseDTO response = new ProductResponseDTO();
        response.setProductId(1L);
        response.setName("Mouse");
        response.setPrice(price);

        when(productRepository.findByPrice(price)).thenReturn(List.of(product));
        when(productMapper.toDTO(product)).thenReturn(response);

        List<ProductResponseDTO> result = productService.getProducts(null, price, null);

        assertEquals(1, result.size());
        assertEquals("Mouse", result.get(0).getName());
        assertEquals(price, result.get(0).getPrice());

        verify(productRepository).findByPrice(price);
        verify(productMapper).toDTO(product);
    }

    /**
     * Tests fetching products by quantity.
     */
    @Test
    void getProducts_byQuantity_success() {
        ProductEntity product = new ProductEntity();
        product.setProductId(1L);
        product.setName("Keyboard");
        product.setQuantity(5);

        ProductResponseDTO response = new ProductResponseDTO();
        response.setProductId(1L);
        response.setName("Keyboard");
        response.setQuantity(5);

        when(productRepository.findByQuantity(5)).thenReturn(List.of(product));
        when(productMapper.toDTO(product)).thenReturn(response);

        List<ProductResponseDTO> result = productService.getProducts(null, null, 5);

        assertEquals(1, result.size());
        assertEquals("Keyboard", result.get(0).getName());
        assertEquals(5, result.get(0).getQuantity());

        verify(productRepository).findByQuantity(5);
        verify(productMapper).toDTO(product);
    }

    /**
     * Tests fetching all products.
     */
    @Test
    void getAllProducts_success() {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setName("Laptop");

        ProductEntity product2 = new ProductEntity();
        product2.setProductId(2L);
        product2.setName("Mouse");

        ProductResponseDTO response1 = new ProductResponseDTO();
        response1.setProductId(1L);
        response1.setName("Laptop");

        ProductResponseDTO response2 = new ProductResponseDTO();
        response2.setProductId(2L);
        response2.setName("Mouse");

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(productMapper.toDTO(product1)).thenReturn(response1);
        when(productMapper.toDTO(product2)).thenReturn(response2);

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());

        verify(productRepository).findAll();
        verify(productMapper).toDTO(product1);
        verify(productMapper).toDTO(product2);
    }

    /**
     * Tests updating product by ID.
     */
    @Test
    void updateProductById_success() {
        Long productId = 1L;

        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setName("DELL Laptop HD");
        updateDTO.setDescription("HD SMART DELL LAPTOP");
        updateDTO.setPrice(new BigDecimal("60000"));
        updateDTO.setQuantity(8);

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setProductId(productId);
        existingProduct.setName("Laptop");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(new BigDecimal("55000"));
        existingProduct.setQuantity(10);

        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setProductId(productId);
        savedProduct.setName("DELL Laptop HD");
        savedProduct.setDescription("HD SMART DELL LAPTOP");
        savedProduct.setPrice(new BigDecimal("60000"));
        savedProduct.setQuantity(8);

        ProductResponseDTO response = new ProductResponseDTO();
        response.setProductId(productId);
        response.setName("DELL Laptop HD");
        response.setDescription("HD SMART DELL LAPTOP");
        response.setPrice(new BigDecimal("60000"));
        response.setQuantity(8);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(response);

        ProductResponseDTO result = productService.updateProductById(productId, updateDTO);

        assertNotNull(result);
        assertEquals("DELL Laptop HD", result.getName());
        assertEquals(new BigDecimal("60000"), result.getPrice());
        assertEquals(8, result.getQuantity());

        verify(productRepository).findById(productId);
        verify(productRepository).save(existingProduct);
        verify(productMapper).toDTO(savedProduct);
    }

    /**
     * Tests deleting product by ID.
     */
    @Test
    void deleteProductById_success() {
        Long productId = 1L;

        ProductEntity product = new ProductEntity();
        product.setProductId(productId);
        product.setName("Laptop");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.deleteProductById(productId));

        verify(productRepository).findById(productId);
        verify(productRepository).delete(product);
    }
}