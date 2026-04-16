package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.LoggedInUserDTO;
import com.ecommerce.api.dto.OrderItemRequestDTO;
import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.entity.OrderEntity;
import com.ecommerce.api.entity.OrderItemEntity;
import com.ecommerce.api.entity.OrderStatusEntity;
import com.ecommerce.api.entity.ProductEntity;
import com.ecommerce.api.entity.UserEntity;
import com.ecommerce.api.enums.OrderStatus;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.mapper.OrderMapper;
import com.ecommerce.api.repository.OrderItemRepository;
import com.ecommerce.api.repository.OrderRepository;
import com.ecommerce.api.repository.OrderStatusRepository;
import com.ecommerce.api.repository.ProductRepository;
import com.ecommerce.api.repository.UserRepository;
import com.ecommerce.api.service.OrderService;
import com.ecommerce.api.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link OrderServiceImpl}.
 *
 * - Creating an order
 * - Handling product not found and out-of-stock scenarios
 * - Fetching orders using different filters (status, user, amount, quantity)
 * - Fetching orders of logged-in user
 * - Cancelling an order and restoring product quantity
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    /**
     * mocked UserRepository
     */
    @Mock
    private UserRepository userRepository;

    /**
     * mocked LoggedInUserServiceImpl
     */
    @Mock
    private LoggedInUserServiceImpl loggedInUserServiceImpl;

    /**
     * mocked ProductRepository
     */
    @Mock
    private ProductRepository productRepository;

    /**
     * mocked OrderRepository
     */
    @Mock
    private OrderRepository orderRepository;

    /**
     * mocked OrderStatusRepository
     */
    @Mock
    private OrderStatusRepository orderStatusRepository;

    /**
     * mocked OrderItemRepository
     */
    @Mock
    private OrderItemRepository orderItemRepository;
    /**
     * mocked Mapper
     */
    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderService self;

    /**
     OrderServiceImpl with mocked dependencies injected.
     */
    @InjectMocks
    private OrderServiceImpl orderService;

    /**
     * Tests successful order creation.
     * Verifies:
     * - Logged-in user is fetched correctly
     * - Products are fetched and validated
     * - Order and order items are saved
     * - Product quantity is reduced
     */
    @Test
    void createOrder_success() {
        String username = "ramPatel";

        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setItems(List.of(itemRequest));

        LoggedInUserDTO currentUser = new LoggedInUserDTO(username, AppConstants.ROLE_USER);

        UserEntity user = new UserEntity();
        user.setUsername(username);

        ProductEntity product = new ProductEntity();
        product.setProductId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(50000));
        product.setQuantity(5);

        OrderStatusEntity placedStatus = new OrderStatusEntity();
        placedStatus.setStatusName(OrderStatus.PLACED.name());

        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setOrderId(10L);
        savedOrder.setUser(user);
        savedOrder.setStatus(placedStatus);
        savedOrder.setTotalAmount(BigDecimal.valueOf(100000));
        savedOrder.setTotalQuantity(2);

        OrderItemEntity savedItem = new OrderItemEntity();
        savedItem.setOrder(savedOrder);
        savedItem.setProduct(product);
        savedItem.setQuantity(2);

        OrderResponseDTO responseDTO = new OrderResponseDTO();

        when(loggedInUserServiceImpl.getCurrentUser()).thenReturn(currentUser);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(orderStatusRepository.findByStatusName(OrderStatus.PLACED.name())).thenReturn(Optional.of(placedStatus));

        when(productRepository.findAllById(anySet())).thenReturn(List.of(product));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItemEntity.class))).thenReturn(savedItem);
        when(orderMapper.toDTO(savedOrder, List.of(savedItem))).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(responseDTO, result);

        verify(userRepository).findByUsername(username);
        verify(orderStatusRepository).findByStatusName(OrderStatus.PLACED.name());
        verify(productRepository).findAllById(anySet());

        verify(orderRepository).save(any(OrderEntity.class));
        verify(orderItemRepository).save(any(OrderItemEntity.class));
        verify(productRepository).save(product);
        verify(orderMapper).toDTO(savedOrder, List.of(savedItem));

    }

    /**
     * Tests fetching orders by total amount range.
     */
    @Test
    void getOrders_byAmountRange_success() {
        OrderEntity order = new OrderEntity();
        OrderResponseDTO responseDTO = new OrderResponseDTO();

        BigDecimal min = BigDecimal.valueOf(1000);
        BigDecimal max = BigDecimal.valueOf(5000);

        when(orderRepository.findByTotalAmountBetween(min, max)).thenReturn(List.of(order));
        when(orderItemRepository.findByOrderIn(anyList())).thenReturn(List.of());
        when(orderMapper.toDTO(order, List.of())).thenReturn(responseDTO);

        List<OrderResponseDTO> result =
                orderService.getOrders(null, null, min, max, null);

        assertEquals(1, result.size());
        verify(orderRepository).findByTotalAmountBetween(min, max);
    }

    /**
     * Tests fetching orders by minimum quantity.
     */
    @Test
    void getOrders_byMinQuantity_success() {
        OrderEntity order = new OrderEntity();
        OrderResponseDTO responseDTO = new OrderResponseDTO();

        when(orderRepository.findByTotalQuantityGreaterThanEqual(2)).thenReturn(List.of(order));
        when(orderItemRepository.findByOrderIn(anyList())).thenReturn(List.of());
        when(orderMapper.toDTO(order, List.of())).thenReturn(responseDTO);

        List<OrderResponseDTO> result =
                orderService.getOrders(null, null, null, null, 2);

        assertEquals(1, result.size());
        verify(orderRepository).findByTotalQuantityGreaterThanEqual(2);
    }

    /**
     * Tests fetching all orders when user is ADMIN.
     *
     */
    @Test
    void returnsAllOrders_admin() {
        OrderResponseDTO responseDTO = new OrderResponseDTO();

        when(loggedInUserServiceImpl.getUsername()).thenReturn("admin");
        when(loggedInUserServiceImpl.getRole()).thenReturn(AppConstants.ROLE_ADMIN);

        when(self.getAllOrders()).thenReturn(List.of(responseDTO));
        List<OrderResponseDTO> result =
                orderService.getOrders(null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));

        verify(self).getAllOrders();
    }

    /**
     * Tests fetching all orders when user Role is USER .
     *
     */
    @Test
    void getOrders_normalUser() {
        OrderResponseDTO responseDTO = new OrderResponseDTO();

        when(loggedInUserServiceImpl.getUsername()).thenReturn("ramPatel");
        when(loggedInUserServiceImpl.getRole()).thenReturn(AppConstants.ROLE_USER);

        when(self.getOrdersByUser()).thenReturn(List.of(responseDTO));
        List<OrderResponseDTO> result =
                orderService.getOrders(null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));

        verify(self).getOrdersByUser();
    }

    /**
     * Tests fetching all orders
     */
    @Test
    void getAllOrders_success() {
        OrderEntity order = new OrderEntity();
        OrderResponseDTO responseDTO = new OrderResponseDTO();

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderItemRepository.findByOrderIn(anyList())).thenReturn(List.of());
        when(orderMapper.toDTO(order, List.of())).thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));
        verify(orderRepository).findAll();
    }

    /**
     * Test fetching order by username
     */
    @Test
    void getOrdersByUser_success() {
        String username = "ramPatel";
        LoggedInUserDTO currentUser = new LoggedInUserDTO(username, AppConstants.ROLE_USER);

        OrderResponseDTO responseDTO = new OrderResponseDTO();

        when(loggedInUserServiceImpl.getCurrentUser()).thenReturn(currentUser);
        when(self.getOrdersByUsername(username)).thenReturn(List.of(responseDTO));
        List<OrderResponseDTO> result = orderService.getOrdersByUser();

        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));

        verify(self).getOrdersByUsername(username);
    }
    /**
     * Tests successful order cancellation.
     */
    @Test
    void cancelOrder_success() {
        String username = "ramPatel";

        LoggedInUserDTO currentUser = new LoggedInUserDTO(username, AppConstants.ROLE_USER);

        UserEntity user = new UserEntity();
        user.setUsername(username);

        ProductEntity product = new ProductEntity();
        product.setProductId(1L);
        product.setName("Laptop");
        product.setQuantity(3);

        OrderStatusEntity placedStatus = new OrderStatusEntity();
        placedStatus.setStatusName(OrderStatus.PLACED.name());

        OrderStatusEntity cancelledStatus = new OrderStatusEntity();
        cancelledStatus.setStatusName(OrderStatus.CANCELLED.name());

        OrderEntity order = new OrderEntity();
        order.setOrderId(1L);
        order.setUser(user);
        order.setStatus(placedStatus);

        OrderItemEntity item = new OrderItemEntity();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(2);

        OrderResponseDTO responseDTO = new OrderResponseDTO();

        when(loggedInUserServiceImpl.getCurrentUser()).thenReturn(currentUser);
        when(loggedInUserServiceImpl.isAdmin()).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByStatusName(OrderStatus.CANCELLED.name()))
                .thenReturn(Optional.of(cancelledStatus));
        when(orderItemRepository.findByOrder(order)).thenReturn(List.of(item));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDTO(order, List.of(item))).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        assertEquals(5, product.getQuantity());
        assertEquals(cancelledStatus, order.getStatus());


        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    /**
     * Tests cancelling order when order is already delivered.
     */
    @Test
    void cancelOrder_orderNotFound_throwsException() {
        LoggedInUserDTO currentUser = new LoggedInUserDTO("ramPatel", AppConstants.ROLE_USER);

        when(loggedInUserServiceImpl.getCurrentUser()).thenReturn(currentUser);
        when(loggedInUserServiceImpl.isAdmin()).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.cancelOrder(1L)
        );

        assertEquals(AppConstants.ORDER_NOT_FOUND, exception.getMessage());
    }


}