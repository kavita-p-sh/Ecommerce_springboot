package com.ecommerce.api.controller;

import com.ecommerce.api.dto.OrderRequestDTO;
import com.ecommerce.api.dto.OrderResponseDTO;
import com.ecommerce.api.service.OrderService;
import com.ecommerce.api.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Order Controller",description = "APIs for managing Orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * create a new order
     * @param request contains order details such as products, quantity.
     * @return order detail
     */
    @PostMapping
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN})
    @Operation(summary = "Create order",
            description = "Creates a new order with products and quantity.")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO request) {

        log.info("Order creation request");

        OrderResponseDTO response = orderService.createOrder(request);

        log.info("Order created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Fetch orders with optional filters.
     * Admin/Manager can fetch all orders.
     * User can fetch their own orders.
     *
     * @param status order status
     * @param createdBy user who created the order
     * @param minAmount minimum total amount
     * @param maxAmount maximum total amount
     * @param minQuantity minimum total quantity
     * @return list of filtered orders
     */
    @GetMapping
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN, AppConstants.ROLE_MANAGER})
    @Operation(summary = "Get Orders",
               description ="Fetch orders with filters like status,user, amount and quantity.Admin or Manager can view all orders, users can view their own orders only." )
    public ResponseEntity<List<OrderResponseDTO>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) Integer minQuantity) {

        log.info("Fetching orders with filters - status: {}, createdBy: {}, minAmount: {}, maxAmount: {}, minQuantity: {}",
                status, createdBy, minAmount, maxAmount, minQuantity);

        return ResponseEntity.ok(
                orderService.getOrders(status, createdBy, minAmount, maxAmount, minQuantity)
        );
    }

    /**
     * cancel order by id
     * @param orderId id of order
     * @return updated order after cancellation
     */
    @PutMapping("/cancel/{orderId}")
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN, AppConstants.ROLE_MANAGER})
    @Operation(summary = "Cancel Order",
               description = "Cancel an order by ID,Users can cancel their own orders ,Admins can cancel any order on behalf of a user if needed")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        log.warn("Cancelling order with id {}", orderId);

        OrderResponseDTO response=orderService.cancelOrder(orderId);

        log.info("Order cancelled successfully with id: {}", orderId);

        return ResponseEntity.ok(response);
    }
}