package com.ecommerce.api.controller;

import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.service.OrderService;
import com.ecommerce.api.util.AppConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * handles order related APIs
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class
OrderController {

    private final OrderService orderService;

    /**
     * create a new order
     * @param
     * @return order detail
     */
    @PostMapping
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN})
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO request) {

        log.info("Order creation request");

        OrderResponseDTO response = orderService.createOrder(request);

        log.info("Order created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get orders.
     * Admin -gets all orders
     * User -gets only their orders
     * filtering
     * @return list of orders
     */
     @GetMapping
     @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN})
     public ResponseEntity<List<OrderResponseDTO>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) BigDecimal totalAmount,
            @RequestParam(required = false) Integer totalQuantity) {

        log.info("Fetching orders with filters");

        return ResponseEntity.ok(
                orderService.getOrders(status, createdBy, totalAmount, totalQuantity)
        );
    }
    /**
     * cacel order by id
     * @param orderId id of order
     * @return updated order after cancellation
     */
    @PutMapping("/cancel/{orderId}")
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN, AppConstants.ROLE_MANAGER})
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        log.warn("Cancelling order with id {}", orderId);

        OrderResponseDTO response=orderService.cancelOrder(orderId);

        log.info("Order cancelled successfully with id: {}", orderId);

        return ResponseEntity.ok(response);
    }
}