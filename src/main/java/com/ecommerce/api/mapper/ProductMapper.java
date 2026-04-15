package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.ProductResponseDTO;
import com.ecommerce.api.entity.ProductEntity;
import org.springframework.stereotype.Component;

/**
 * Converts ProductEntity to ProductResponseDTO.
 * @return ProductResponseDTO containing product details
 */
@Component
public class ProductMapper {

    public ProductResponseDTO toDTO(ProductEntity product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCreatedBy(product.getCreatedBy());
        dto.setCreatedTimestamp(product.getCreatedTimestamp());
        dto.setUpdatedBy(product.getUpdatedBy());
        dto.setUpdatedTimestamp(product.getUpdatedTimestamp());

        return dto;
    }
}