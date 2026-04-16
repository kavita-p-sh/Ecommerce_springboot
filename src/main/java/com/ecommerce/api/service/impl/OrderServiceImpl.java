package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.OrderItemRequestDTO;
import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.enums.OrderStatus;
import com.ecommerce.api.exception.BadRequestException;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.OrderMapper;
import com.ecommerce.api.service.LoggedInUserService;
import com.ecommerce.api.service.OrderService;
import com.ecommerce.api.util.AppConstants;
import com.ecommerce.api.entity.*;
import com.ecommerce.api.repository.*;
import com.ecommerce.api.util.CacheConstant;
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
    private final LoggedInUserService loggedInUserService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderService self;

    /**
     * Creates a new order for the logged-in user.
     * Validates items, calculates totals, and saves order + items.
     *
     * @param request order request containing productId and quantity
     * @return OrderResponseDTO with order details
     */
    @Override
    @Transactional
    @CacheEvict(value = {CacheConstant.ORDERS,CacheConstant.ORDERS_BY_USER}, allEntries = true)
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        String username = loggedInUserService.getCurrentUser().getUsername();
        log.info("Creating order for user: {}", username);
        UserEntity user = getUser(username);

        OrderStatusEntity placedStatus = getOrderStatus(OrderStatus.PLACED.name());

        Map<Long, ProductEntity> productMap = getProductMap(request.getItems());

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setStatus(placedStatus);


        setOrderTotals(request.getItems(), productMap, order);

        OrderEntity savedOrder = orderRepository.save(order);

        List<OrderItemEntity> savedItems = saveOrderItems(request.getItems(), savedOrder, productMap, username);

        return orderMapper.toDTO(savedOrder, savedItems);
    }
    /**
     * Calculates total amount and total quantity of order.
     */
    private void setOrderTotals(List<OrderItemRequestDTO> items,
                                Map<Long, ProductEntity> productMap,
                                OrderEntity order) {

        log.debug("Calculating order totals for {} items", items.size());

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (OrderItemRequestDTO item : items) {
            ProductEntity product = productMap.get(item.getProductId());

            if (product == null) {
                log.error("Product not found for productId: {}", item.getProductId());
                throw new ResourceNotFoundException(AppConstants.PRODUCT_NOT_FOUND);
            }

            if (product.getQuantity() < item.getQuantity()) {
                log.warn("Out of stock for product: {}", product.getName());
                throw new BadRequestException(AppConstants.OUT_OF_STOCK + product.getName());
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
            totalQuantity += item.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQuantity);
        log.debug("Total amount: {}, Total quantity: {}", totalAmount, totalQuantity);

    }

    /**
     * Saves order items and updates product stock.
     */
    private List<OrderItemEntity> saveOrderItems(List<OrderItemRequestDTO> items,
                                                 OrderEntity order,
                                                 Map<Long, ProductEntity> productMap,
                                                 String username) {

        return items.stream()
                .map(item -> {
                    ProductEntity product = productMap.get(item.getProductId());

                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    productRepository.save(product);

                    OrderItemEntity orderItem = new OrderItemEntity();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(item.getQuantity());

                    return orderItemRepository.save(orderItem);
                })
                .toList();
    }
    private Map<Long, List<OrderItemEntity>> getOrderItemsMap(List<OrderEntity> orders) {
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrderIn(orders);

        return orderItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getOrderId()));
    }
    private List<OrderResponseDTO> mapOrdersToDTO(List<OrderEntity> orders) {
        Map<Long, List<OrderItemEntity>> orderItemsMap = getOrderItemsMap(orders);

        return orders.stream()
                .map(order -> orderMapper.toDTO(
                        order,
                        orderItemsMap.getOrDefault(order.getOrderId(), Collections.emptyList())
                ))
                .toList();
    }

    /**
     * Fetches orders based on optional filters like status, user,
     * total amount or quantity.
     *
     * @param status filter by order status
     * @param createdBy filter by username
     * @param minAmount filter by total amount
     * @param minQuantity filter by quantity
     * @return list of filtered orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrders(String status, String createdBy, BigDecimal minAmount, BigDecimal maxAmount, Integer minQuantity) {

        log.info("Fetching orders with filters - status: {}, createdBy: {}, minAmount: {}, maxAmount: {}, minQuantity: {}",
                status, createdBy, minAmount, maxAmount, minQuantity);

        if (status != null && !status.isBlank()) {
            return mapOrdersToDTO(orderRepository.findByStatus_StatusName(status));

        }

        if (createdBy != null && !createdBy.isBlank()) {
            return mapOrdersToDTO(orderRepository.findByCreatedBy(createdBy));

        }

        if (minAmount != null && maxAmount != null) {
            return mapOrdersToDTO(orderRepository.findByTotalAmountBetween(minAmount, maxAmount));

        }

        if (minQuantity != null) {
            return mapOrdersToDTO(orderRepository.findByTotalQuantityGreaterThanEqual(minQuantity));

        }

        String username = loggedInUserService.getUsername();
        String role = loggedInUserService.getRole();

        if (AppConstants.ROLE_ADMIN.equals(role) || AppConstants.ROLE_MANAGER.equals(role)) {
            return self.getAllOrders();
        }

        return self.getOrdersByUser();
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
    @Cacheable(value = CacheConstant.ORDERS, key = CacheConstant.ALL_ORDERS_KEY)
    public List<OrderResponseDTO> getAllOrders() {
        return mapOrdersToDTO(orderRepository.findAll());

    }

    /**
     * Fetches orders of currently logged-in user.
     * @return list of user-specific orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUser() {
        String username = loggedInUserService.getCurrentUser().getUsername();
        return self.getOrdersByUsername(username);

    }
    /**
     * Fetches orders for the given username.
     *
     * @param username logged-in username
     * @return list of user-specific orders
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstant.ORDERS, key = CacheConstant.ORDERS_BY_USER_KEY)
    public List<OrderResponseDTO> getOrdersByUsername(String username) {
        UserEntity user = getUser(username);
        return mapOrdersToDTO(orderRepository.findByUser(user));
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
    @CacheEvict(value = {CacheConstant.ORDERS, CacheConstant.ORDERS_BY_USER}, allEntries = true)
    public OrderResponseDTO cancelOrder(Long orderId) {
        String username = loggedInUserService.getCurrentUser().getUsername();
        boolean isAdmin = loggedInUserService.isAdmin();


        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND));

        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new BadRequestException(AppConstants.NOT_ALLOWED_TO_CANCEL_ORDER);
        }

        if (OrderStatus.DELIVERED.name().equals(order.getStatus().getStatusName())) {
            throw new BadRequestException(AppConstants.CANNOT_CANCEL);
        }

        if (OrderStatus.CANCELLED.name().equals(order.getStatus().getStatusName())) {
            throw new BadRequestException("Order is already cancelled");
        }

        OrderStatusEntity cancelledStatus = getOrderStatus(OrderStatus.CANCELLED.name());

        List<OrderItemEntity> items = orderItemRepository.findByOrder(order);

        for (OrderItemEntity item : items) {
            ProductEntity product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(cancelledStatus);

        OrderEntity updatedOrder = orderRepository.save(order);

        log.info("Order cancelled successfully. Order id: {}, user: {}", orderId, username);

        return orderMapper.toDTO(updatedOrder, items);
    }


}








