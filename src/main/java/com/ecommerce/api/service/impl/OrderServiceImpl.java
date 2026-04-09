package com.ecommerce.api.service.impl;


import com.ecommerce.api.dto.OrderItemRequestDTO;
import com.ecommerce.api.dto.OrderItemResponseDTO;
import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.enums.OrderStatusEnum;
import com.ecommerce.api.exception.BadRequestException;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.service.OrderService;
import com.ecommerce.api.util.AppConstants;
import com.ecommerce.api.entity.*;
import com.ecommerce.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final LoggedInUserServiceImpl loggedInUserServiceImpl;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Creates a new order for the logged-in user.
     * Validates items, calculates totals, and saves order + items.
     *
     * @param request order request containing productId and quantity
     * @return OrderResponseDTO with order details
     */
    @Override
    @Transactional
    @CacheEvict(value = {"orders", "ordersByUser"}, allEntries = true)
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        validateOrderItems(request.getItems());

        UserEntity user = getUser(username);
        OrderStatusEntity placedStatus = getOrderStatus(OrderStatusEnum.PLACED.name());

        Map<Long, ProductEntity> productMap = getProductMap(request.getItems());

        OrdersEntity order = new OrdersEntity();
        order.setUser(user);
        order.setStatus(placedStatus);
        order.setCreatedBy(username);

        setOrderTotals(request.getItems(), productMap, order);

        OrdersEntity savedOrder = orderRepository.save(order);

        List<OrderItemEntity> savedItems = saveOrderItems(request.getItems(), savedOrder, productMap, username);

        return mapToDTO(savedOrder, savedItems);
    }
    /**
     * Calculates total amount and total quantity of order.
     */
    private void setOrderTotals(List<OrderItemRequestDTO> items,
                                Map<Long, ProductEntity> productMap,
                                OrdersEntity order) {

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (OrderItemRequestDTO item : items) {
            ProductEntity product = productMap.get(item.getProductId());

            if (product == null) {
                throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND);
            }

            if (product.getQuantity() < item.getQuantity()) {
                throw new BadRequestException(AppConstants.OUT_OF_STOCK + product.getName());
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
            totalQuantity += item.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQuantity);
    }

    /**
     * Saves order items and updates product stock.
     */
    private List<OrderItemEntity> saveOrderItems(List<OrderItemRequestDTO> items,
                                                 OrdersEntity order,
                                                 Map<Long, ProductEntity> productMap,
                                                 String username) {

        return items.stream()
                .map(item -> {
                    ProductEntity product = productMap.get(item.getProductId());

                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    product.setUpdatedBy(username);
                    productRepository.save(product);

                    OrderItemEntity orderItem = new OrderItemEntity();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(item.getQuantity());

                    return orderItemRepository.save(orderItem);
                })
                .toList();
    }

    /**
     * Validates order items.
     * Checks for null values and invalid quantity.
     */
    private void validateOrderItems(List<OrderItemRequestDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new BadRequestException(AppConstants.ORDER_ITEMS_CAN_NOT_EMPTY);
        }

        for (OrderItemRequestDTO item : items) {
            if (item.getProductId() == null) {
                throw new BadRequestException(AppConstants.PRODUCT_NOT_FOUND);
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BadRequestException(AppConstants.PRODUCT_QUANTITY_INVALID);
            }
        }
    }

    /**
     * Converts Order entity to DTO.
     */
    private OrderResponseDTO mapToDTO(OrdersEntity order) {
        List<OrderItemEntity> items = orderItemRepository.findByOrder(order);
        return mapToDTO(order, items);
    }

    /**
     * Converts Order + Items into response DTO.
     */
    private OrderResponseDTO mapToDTO(OrdersEntity order, List<OrderItemEntity> items) {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setTotalAmount(order.getTotalAmount());
        response.setTotalQuantity(order.getTotalQuantity());
        response.setStatus(order.getStatus().getStatusName());
        response.setCreatedBy(order.getCreatedBy());
        response.setCreatedTimeStamp(order.getCreatedTimeStamp());
        response.setUpdatedTimeStamp(order.getUpdatedTimeStamp());
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
    /**
     * Fetches orders based on optional filters like status, user,
     * total amount or quantity.
     *
     * @param status filter by order status
     * @param createdBy filter by username
     * @param totalAmount filter by total amount
     * @param totalQuantity filter by quantity
     * @return list of filtered orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrders(String status, String createdBy, BigDecimal totalAmount, Integer totalQuantity) {

        log.info("Fetching orders with filters - status: {}, createdBy: {}, totalAmount: {}, totalQuantity: {}",
                status, createdBy, totalAmount, totalQuantity);

        if (status != null && !status.isBlank()) {
            return orderRepository.findByStatus_StatusName(status)
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        if (createdBy != null && !createdBy.isBlank()) {
            return orderRepository.findByCreatedBy(createdBy)
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        if (totalAmount != null) {
            return orderRepository.findByTotalAmount(totalAmount)
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        if (totalQuantity != null) {
            return orderRepository.findByTotalQuantity(totalQuantity)
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        return getAllOrders();
    }
    /**
     * Fetch user by username.
     */
    private UserEntity getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
    }

    /**
     * Fetch order status entity by status name.
     */
    private OrderStatusEntity getOrderStatus(String statusName) {
        return orderStatusRepository.findByStatusName(statusName)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.STATUS_NOT_FOUND));
    }

    /**
     * Fetch all products in one query and map them by productId.
     * (Fix for N+1 query problem)
     */
    private Map<Long, ProductEntity> getProductMap(List<OrderItemRequestDTO> items) {
        Set<Long> productIds = items.stream()
                .map(OrderItemRequestDTO::getProductId)
                .collect(Collectors.toSet());

        List<ProductEntity> products = productRepository.findAllById(productIds);

        Map<Long, ProductEntity> productMap = new HashMap<>();
        for (ProductEntity product : products) {
            productMap.put(product.getProductId(), product);
        }

        if (productMap.size() != productIds.size()) {
            throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND);
        }

        return productMap;
    }

    /**
     * Fetches all orders from database.
     * Uses caching for better performance.
     *
     * @return list of all orders
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "'AllOrders'")
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Fetches orders of currently logged-in user.
     * Uses cache based on username.
     *
     * @return list of user-specific orders
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ordersByUser", key = "#root.methodName + '_' + T(java.util.Objects).hash(#root.target.loggedInUserServiceImpl.getCurrentUser().getUsername())")
    public List<OrderResponseDTO> getOrdersByUser() {
        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        UserEntity user = getUser(username);

        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Cancels an order if it belongs to the logged-in user
     * and is not already delivered or cancelled.
     *
     * Also restores product stock.
     *
     * @param orderId id of order to cancel
     * @return updated order response
     */
    @Override
    @Transactional
    @CacheEvict(value = {"orders", "ordersByUser"}, allEntries = true)
    public OrderResponseDTO cancelOrder(Long orderId) {
        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        OrdersEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND));

        if (!order.getUser().getUsername().equals(username)) {
            throw new BadRequestException(AppConstants.NOT_ALLOWED_TO_CANCEL_ORDER);
        }

        if (OrderStatusEnum.DELIVERED.name().equals(order.getStatus().getStatusName())) {
            throw new BadRequestException(AppConstants.CANNOT_CANCEL);
        }

        if (OrderStatusEnum.CANCELLED.name().equals(order.getStatus().getStatusName())) {
            throw new BadRequestException("Order is already cancelled");
        }

        OrderStatusEntity cancelledStatus = getOrderStatus(OrderStatusEnum.CANCELLED.name());

        List<OrderItemEntity> items = orderItemRepository.findByOrder(order);

        for (OrderItemEntity item : items) {
            ProductEntity product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            product.setUpdatedBy(username);
            productRepository.save(product);
        }

        order.setStatus(cancelledStatus);
        order.setUpdatedBy(username);

        OrdersEntity updatedOrder = orderRepository.save(order);

        log.info("Order cancelled successfully. Order id: {}, user: {}", orderId, username);

        return mapToDTO(updatedOrder, items);
    }


}








