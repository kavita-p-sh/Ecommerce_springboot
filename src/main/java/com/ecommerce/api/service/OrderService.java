package com.ecommerce.api.service;

import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;

import java.math.BigDecimal;
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

    /**
     * Creates a new order.
     *
     * @param request order request data
     * @return created order details
     */
    OrderResponseDTO createOrder(OrderRequestDTO request);

    /**
     * Fetch orders based on optional filters.
     *
     * @param status order status
     * @param createdBy user who created the order
     * @param minAmount minimum total amount
     * @param maxAmount maximum total amount
     * @param minQuantity minimum total quantity
     * @return list of filtered orders
     */
    List<OrderResponseDTO> getOrders(String status,
                                     String createdBy,
                                     BigDecimal minAmount,
                                     BigDecimal maxAmount,
                                     Integer minQuantity);
    /**
     * fetch all orders.
     * @return list of orders
     */
    List<OrderResponseDTO> getAllOrders();

    /**
     * fetch orders of current logged-in User
     * @return
     */
    List<OrderResponseDTO> getOrdersByUser();

    /**
     * Cancels an Order  by id
     * @param orderId order id
     * @return cancelled order details
     */
    OrderResponseDTO cancelOrder(Long orderId);
}