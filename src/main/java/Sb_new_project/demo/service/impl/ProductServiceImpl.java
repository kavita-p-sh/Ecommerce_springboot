package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import Sb_new_project.demo.dto.ProductRequestDTO;
import Sb_new_project.demo.dto.ProductResponseDTO;
import Sb_new_project.demo.dto.ProductUpdateDTO;
import Sb_new_project.demo.entity.Product;
import Sb_new_project.demo.exception.ResourceNotFoundException;
import Sb_new_project.demo.repository.ProductRepository;
import Sb_new_project.demo.service.ProductService;
import Sb_new_project.demo.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ProductService.
 * Handles business logic for product operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final LoggedInUserServiceImpl loggedInUserServiceImpl;

    /**
     * Create new product.
     */
    @Override
    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO requestDTO) {

        log.info("Adding product: {}", requestDTO.getName());

        if (productRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException(Constant.PRODUCT_ALREADY_EXISTS + requestDTO.getName());
        }

        Product product = new Product();
        product.setName(requestDTO.getName().trim());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());

        LoggedInUserDTO user = loggedInUserServiceImpl.getCurrentUser();
        product.setCreatedBy(user.getUsername());

        return mapToResponse(productRepository.save(product));
    }

    /**
     * Get all products.
     */
    @Override
    public List<ProductResponseDTO> getAllProducts() {

        List<ProductResponseDTO> responseList = new ArrayList<>();

        for (Product product : productRepository.findAll()) {
            responseList.add(mapToResponse(product));
        }

        return responseList;
    }

    /**
     * Get product by name.
     */
    @Override
    public ProductResponseDTO getProductByName(String name) {

        Product product = productRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + name));

        return mapToResponse(product);
    }

    /**
     * Update product by name.
     */
    @Override
    @Transactional
    public ProductResponseDTO updateProductByName(String name, ProductUpdateDTO dto) {

        log.info("Updating product: {}", name);

        Product product = productRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + name));

        LoggedInUserDTO user = loggedInUserServiceImpl.getCurrentUser();

        if (!user.getRoles().contains(Constant.ROLE_ADMIN)) {
            throw new AccessDeniedException(Constant.ONLY_ADMIN_ALLOWED);
        }

        // Update fields
        if (StringUtils.hasText(dto.getName())) {

            if (!product.getName().equals(dto.getName()) &&
                    productRepository.existsByName(dto.getName())) {

                throw new IllegalArgumentException(Constant.PRODUCT_ALREADY_EXISTS + dto.getName());
            }

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

        product.setUpdatedBy(user.getUsername());

        return mapToResponse(productRepository.save(product));
    }

    /**
     * Delete product by name.
     */
    @Override
    @Transactional
    public void deleteProductByName(String name) {

        log.info("Deleting product: {}", name);

        Product product = productRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + name));

        productRepository.delete(product);
    }

    /**
     * Convert entity to response DTO.
     */
    private ProductResponseDTO mapToResponse(Product product) {

        ProductResponseDTO responseDTO = new ProductResponseDTO();

        responseDTO.setProductId(product.getProductId());
        responseDTO.setName(product.getName());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setQuantity(product.getQuantity());
        responseDTO.setCreatedDate(product.getCreatedDate());
        responseDTO.setUpdatedBy(product.getUpdatedBy());

        return responseDTO;
    }
}