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
     * Fetch orders based on filters like status, user, amount or quantity.
     * @param status order
     * @param createdBy user who create order
     * @param totalAmount totalamount  total order amount
     * @param totalQuantity totalQuantity total quantity
     * @return list of orders
     */
    List<OrderResponseDTO> getOrders(String status,
                                     String createdBy,
                                     BigDecimal totalAmount,
                                     Integer totalQuantity);

    /**
     * fetach all orders.
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