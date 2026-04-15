package com.ecommerce.api.controller;

import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.exception.BadRequestException;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for OrderController.
 */

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    /**
     * Tests successful order creation.
     * <p>
     * Verifies:
     * - HTTP status is 201 (Created)
     * - Response body is not null
     * - Returned response matches expected order response
     * - OrderService.createOrder() is called once
     */
    @Test
    public void createOrder_Success() {

        OrderRequestDTO request = new OrderRequestDTO();

        OrderResponseDTO response = new OrderResponseDTO();
        when(orderService.createOrder(request)).thenReturn(response);

        ResponseEntity<OrderResponseDTO> result = orderController.createOrder(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(orderService, times(1)).createOrder(request);
    }


    /**
     * Tests order creation when service throws exception.
     */
    @Test
    void createOrder_Failure() {
        OrderRequestDTO request = new OrderRequestDTO();

        when(orderService.createOrder(request))
                .thenThrow(new BadRequestException("Invalid order request"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderController.createOrder(request)
        );

        assertEquals("Invalid order request", exception.getMessage());
        verify(orderService, times(1)).createOrder(request);
    }

    /**
     * Tests fetching orders using all filters.
     */
    @Test
    void getOrders_Success() {
        String status = "PLACED";
        String createdBy = "RamPatel";
        BigDecimal minAmount = new BigDecimal("100.00");
        BigDecimal maxAmount = new BigDecimal("1000.00");
        Integer minQuantity = 2;

        OrderResponseDTO response1 = new OrderResponseDTO();
        OrderResponseDTO response2 = new OrderResponseDTO();
        List<OrderResponseDTO> responseList = List.of(response1, response2);

        when(orderService.getOrders(status, createdBy, minAmount, maxAmount, minQuantity))
                .thenReturn(responseList);

        ResponseEntity<List<OrderResponseDTO>> result =
                orderController.getOrders(status, createdBy, minAmount, maxAmount, minQuantity);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals(responseList, result.getBody());

        verify(orderService, times(1))
                .getOrders(status, createdBy, minAmount, maxAmount, minQuantity);
    }

    /**
     * Tests fetching orders when service throws exception.
     */
    @Test
    void getOrders_Failure() {
        String status = "PLACED";
        String createdBy = "RamPatel";
        BigDecimal minAmount = new BigDecimal("100.00");
        BigDecimal maxAmount = new BigDecimal("1000.00");
        Integer minQuantity = 2;

        when(orderService.getOrders(status, createdBy, minAmount, maxAmount, minQuantity))
                .thenThrow(new BadRequestException("Invalid filter values"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderController.getOrders(status, createdBy, minAmount, maxAmount, minQuantity)
        );

        assertEquals("Invalid filter values", exception.getMessage());

        verify(orderService, times(1))
                .getOrders(status, createdBy, minAmount, maxAmount, minQuantity);
    }

    /**
     * Tests successful order cancellation.
     */
    @Test
    void cancelOrder_Success() {
        Long orderId = 1L;
        OrderResponseDTO response = new OrderResponseDTO();

        when(orderService.cancelOrder(orderId)).thenReturn(response);

        ResponseEntity<OrderResponseDTO> result = orderController.cancelOrder(orderId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(orderService, times(1)).cancelOrder(orderId);
    }

    /**
     * Tests order cancellation when order is not found.
     */
    @Test
    void cancelOrder_OrderNotFound() {
        Long orderId = 1L;

        when(orderService.cancelOrder(orderId))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderController.cancelOrder(orderId)
        );

        assertEquals("Order not found", exception.getMessage());
        verify(orderService, times(1)).cancelOrder(orderId);
    }

    /**
     * Tests order cancellation when service throws bad request exception.
     */
    @Test
    void cancelOrder_Failure() {
        Long orderId = 1L;

        when(orderService.cancelOrder(orderId))
                .thenThrow(new BadRequestException("Order cannot be cancelled"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> orderController.cancelOrder(orderId)
        );

        assertEquals("Order cannot be cancelled", exception.getMessage());
        verify(orderService, times(1)).cancelOrder(orderId);
    }
}


