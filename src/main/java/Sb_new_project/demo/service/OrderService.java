package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.OrderRequestDTO;
import Sb_new_project.demo.dto.OrderResponseDTO;

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

    OrderResponseDTO createOrder(OrderRequestDTO request);

    List<OrderResponseDTO> getOrdersByUser();

    List<OrderResponseDTO> getAllOrders();

    void cancelOrder(Long orderId);
}