package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;
import com.ecommerce.api.entity.ProductEntity;
import com.ecommerce.api.exception.BadRequestException;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.api.repository.ProductRepository;
import com.ecommerce.api.service.ProductService;
import com.ecommerce.api.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * Adds a new product to the system.
     * Checks if product already exists by name.
     * Saves product in database and returns response DTO.
     *
     * @param requestDTO product request data
     * @return saved product details
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO addProduct(ProductRequestDTO requestDTO) {

        log.info("Adding Product:{} " , requestDTO.getName());

        if (productRepository.existsByName(requestDTO.getName())) {
            throw new BadRequestException(AppConstants.PRODUCT_ALREADY_EXISTS + requestDTO.getName());
        }

        ProductEntity product = new ProductEntity();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());


        return productMapper.toDTO(productRepository.save(product));

    }

    /**
     * Fetch products based on optional filters like name, price or quantity.
     * If no filter is provided, returns all products.
     *
     * @param name product name
     * @param price product price
     * @param quantity product quantity
     * @return list of matching products
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProducts(String name, BigDecimal price, Integer quantity) {

        log.info("Fetching products with filters: name={}, price={}, quantity={}", name, price, quantity);

        if (StringUtils.hasText(name)) {
            log.info("Fetching product by name: {}", name);
            ProductEntity product = productRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + name));
            return List.of(productMapper.toDTO(product));
        }

        if (price != null) {
            log.info("Fetching products by price: {}", price);
            return productRepository.findByPrice(price)
                    .stream()
                    .map(productMapper::toDTO)
                    .toList();
        }

        if (quantity != null) {
            log.info("Fetching products by quantity: {}", quantity);
            return productRepository.findByQuantity(quantity)
                    .stream()
                    .map(productMapper::toDTO)
                    .toList();
        }

        return getAllProducts();
    }

    /**
     * Fetch all products from database.
     * Uses caching to improve performance.
     *
     * @return list of all products
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'allProducts'")
    public List<ProductResponseDTO> getAllProducts() {

        log.info("Fetching all products from database");

        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    /**
     * Updates an existing product by productId
     *
     * @param id the ID of the product to update
     * @param dto the updated product data
     * @return the updated product details
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO updateProductById(Long id, ProductUpdateDTO dto) {

        log.info("Updating product with id: {}", id);

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + id));

        if (StringUtils.hasText(dto.getName())) {
            product.setName(dto.getName().trim());
        }

        if (StringUtils.hasText(dto.getDescription())) {
            product.setDescription(dto.getDescription().trim());
        }

        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }

        if (dto.getQuantity() != null) {
            product.setQuantity(dto.getQuantity());
        }

        ProductEntity savedProduct = productRepository.save(product);

        log.info("Product updated successfully: {}", savedProduct.getName());

        return productMapper.toDTO(savedProduct);
    }
    /**
     * Deletes a product from database using id.
     *
     * @param id product id
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProductById(Long id) {

        log.info("Deleting product with id: {}", id);

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + id));

        productRepository.delete(product);

        log.info("Product deleted successfully with id: {}", id);
    }


}
