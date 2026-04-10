package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.LoggedInUserDTO;
import com.ecommerce.api.dto.ProductRequestDTO;
import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.dto.ProductUpdateDTO;
import com.ecommerce.api.entity.ProductEntity;
import com.ecommerce.api.exception.BadRequestException;
import com.ecommerce.api.exception.ResourceNotFoundException;
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
    private final LoggedInUserServiceImpl loggedInUserServiceImpl;

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


        return mapToResponse(productRepository.save(product));

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
            return List.of(mapToResponse(product));
        }

        if (price != null) {
            log.info("Fetching products by price: {}", price);
            return productRepository.findByPrice(price)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        if (quantity != null) {
            log.info("Fetching products by quantity: {}", quantity);
            return productRepository.findByQuantity(quantity)
                    .stream()
                    .map(this::mapToResponse)
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
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Updates product details using product name.
     * Only provided fields are updated.
     *
     * @param updatedto product update data
     * @return updated product details
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO updateProductByName(ProductUpdateDTO updatedto) {

        log.info("Updating product: {}", updatedto.getName());

        LoggedInUserDTO user = loggedInUserServiceImpl.getCurrentUser();

        ProductEntity product = productRepository.findByName(updatedto.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + updatedto.getName()));

        if (StringUtils.hasText(updatedto.getDescription())) {
            product.setDescription(updatedto.getDescription().trim());
        }

        if (updatedto.getPrice() != null) {
            product.setPrice(updatedto.getPrice());
        }

        if (updatedto.getQuantity() != null) {
            product.setQuantity(updatedto.getQuantity());
        }

        product.setUpdatedBy(user.getUsername());

        ProductEntity savedProduct = productRepository.save(product);

        log.info("Product updated successfully: {}", savedProduct.getName());

        return mapToResponse(savedProduct);
    }

    /**
     * Deletes a product from database using name.
     *
     * @param name product name
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProductByName(String name) {

        log.info("Deleting product: {}", name);

        ProductEntity product = productRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND + name));

        productRepository.delete(product);
    }

    /**
     * Converts ProductEntity to ProductResponseDTO.
     *
     * @param product entity object
     * @return response DTO
     */
    private ProductResponseDTO mapToResponse(ProductEntity product) {

        ProductResponseDTO responseDTO = new ProductResponseDTO();

        responseDTO.setProductId(product.getProductId());
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setQuantity(product.getQuantity());
        responseDTO.setUpdatedBy(product.getUpdatedBy());

        return responseDTO;
    }


}
