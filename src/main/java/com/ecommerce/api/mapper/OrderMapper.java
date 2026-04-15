package com.ecommerce.api.mapper;


import com.ecommerce.api.dto.OrderItemResponseDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.entity.OrderEntity;
import com.ecommerce.api.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper class for converting OrderEntity and OrderItemEntity
 * into OrderResponseDTO.
 */
@Component
public class OrderMapper {

    /**
     * Converts OrderEntity and its items into OrderResponseDTO.
     */
    public OrderResponseDTO toDTO(OrderEntity order, List<OrderItemEntity> items) {

        if (order == null) {
            return null;
        }

        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getOrderId());
        response.setTotalAmount(order.getTotalAmount());
        response.setTotalQuantity(order.getTotalQuantity());
        response.setStatus(order.getStatus().getStatusName());
        response.setCreatedBy(order.getCreatedBy());
        response.setCreatedTimestamp(order.getCreatedTimestamp());
        response.setUpdatedTimestamp(order.getUpdatedTimestamp());
        response.setUpdatedBy(order.getUpdatedBy());

        response.setItems(mapOrderItemsToDTO(items));

        return response;
    }

    /**
     * Converts list of OrderItemEntity to DTO list.
     */
    private List<OrderItemResponseDTO> mapOrderItemsToDTO(List<OrderItemEntity> items) {
        return items.stream()
                .map(item -> {
                    OrderItemResponseDTO dto = new OrderItemResponseDTO();
                    dto.setOrderItemId(item.getOrderItemId());
                    dto.setProductId(item.getProduct().getProductId());
                    dto.setProductName(item.getProduct().getName());
                    dto.setPrice(item.getProduct().getPrice());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                })
                .toList();
    }
}

