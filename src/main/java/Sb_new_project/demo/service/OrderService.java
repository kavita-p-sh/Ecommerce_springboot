package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.OrderResponseDTO;
import Sb_new_project.demo.entity.OrderItemEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing order operations.
 * <p>
 * Provides methods for:
 * - Creating orders
 * - Fetching orders (user/admin)
 * - Fetching specific order
 * - Cancelling orders
 * </p>
 */
public interface OrderService {

    OrderResponseDTO createOrder(List<OrderItemEntity> items);

    List<OrderResponseDTO> getOrdersByUser();

    List<OrderResponseDTO> getOrders(OrderResponseDTO filterDTO);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO cancelOrder(Long orderId);
}