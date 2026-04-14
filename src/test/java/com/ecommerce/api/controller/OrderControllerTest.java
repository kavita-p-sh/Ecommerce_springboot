package com.ecommerce.api.controller;

import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit test class for OrderController.
 *
 */

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private  OrderController orderController;

    /**
     * Tests successful order creation.
     *
     * Verifies:
     * - HTTP status is 201 (Created)
     * - Response body is not null
     * - Returned response matches expected order response
     * - OrderService.createOrder() is called once
     */
    @Test
    public void createOrder_Success(){

        OrderRequestDTO request = new OrderRequestDTO();

        OrderResponseDTO response = new OrderResponseDTO();
        when(orderService.createOrder(request)).thenReturn(response);

        ResponseEntity<OrderResponseDTO> result = orderController.createOrder(request);

        assertNotNull(result);
        assertEquals(201, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(orderService, times(1)).createOrder(request);
    }

    /**
     *Tests fetching orders using all filters.
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
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals(responseList, result.getBody());

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
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(orderService, times(1)).cancelOrder(orderId);
    }
}


