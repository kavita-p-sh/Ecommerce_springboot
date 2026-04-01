package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import Sb_new_project.demo.dto.ProductRequestDTO;
import Sb_new_project.demo.dto.ProductResponseDTO;
import Sb_new_project.demo.dto.ProductUpdateDTO;
import Sb_new_project.demo.entity.Product;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.exception.ResourceNotFoundException;
import Sb_new_project.demo.repository.ProductRepository;
import Sb_new_project.demo.service.ProductService;
import Sb_new_project.demo.util.Constant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final LoggedInUserServiceImpl loggedInUserServiceImpl;

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO addProduct(ProductRequestDTO requestDTO) {

        log.info("Adding Product:{}" + requestDTO.getName());

        if (productRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException(Constant.PRODUCT_ALREADY_EXISTS + requestDTO.getName());
        }

        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());

        LoggedInUserDTO user = loggedInUserServiceImpl.getCurrentUser();
        String username = user.getUsername();

        return mapToResponse(productRepository.save(product));

    }

    @Override
    @Cacheable("products")
    public List<ProductResponseDTO> getAllProducts() {

        log.info("Fetching all products from database");

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "products", key = "#name")
    public ProductResponseDTO getProductByName(String name) {

        log.info("Fetching product by name: {}", name);

        Product product = productRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + name));

        return mapToResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO updateProductByName(String name, ProductUpdateDTO updatedto) {

        log.info("Updating product: {}", name);

        LoggedInUserDTO user = loggedInUserServiceImpl.getCurrentUser();

        if (!user.getRoles().contains(Constant.ROLE_ADMIN)) {
            log.error("Access denied for user: {}", user.getUsername());
            throw new AccessDeniedException(Constant.ONLY_ADMIN_ALLOWED);
        }

        Product product = productRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + name));


        if (updatedto.getName() != null && !updatedto.getName().isEmpty()) {

            if (!product.getName().equals(updatedto.getName())
                    && productRepository.existsByName(updatedto.getName())) {
                throw new IllegalArgumentException(Constant.PRODUCT_ALREADY_EXISTS + updatedto.getName());
            }

            product.setName(updatedto.getName().trim());
        }

        if (updatedto.getDescription() != null && !updatedto.getDescription().isEmpty()) {
            product.setDescription(updatedto.getDescription().trim());
        }

        if (updatedto.getPrice() != null) {
            product.setPrice(updatedto.getPrice());
        }

        if (updatedto.getQuantity() != null) {
            product.setQuantity(updatedto.getQuantity());
        }

        product.setUpdatedBy(user.getUsername());

        Product savedProduct = productRepository.save(product);

        log.info("Product updated successfully: {}", savedProduct.getName());

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProductByName(String name) {

        log.info("Deleting product: {}", name);

        Product product = productRepository.findByName(name)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND + name));

        productRepository.delete(product);
    }

    private ProductResponseDTO mapToResponse(Product product) {

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
