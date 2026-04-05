package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.OrderResponseDTO;
import Sb_new_project.demo.entity.OrderItem;
import Sb_new_project.demo.service.OrderService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * handles order related APIs
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final OrderService orderService;
    /**
     * Add order
     */
    @PostMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody List<OrderItem>dto) {

        log.info("Order creation request");
        OrderResponseDTO response = orderService.createOrder(dto);

        log.info("Order created successfully with id {}", response.getOrderId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        log.info("Fetching orders");
        return ResponseEntity.ok(orderService.getOrders());
    }


    @PutMapping("/cancel/{orderId}")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        log.warn("Cancelling order with id {}", orderId);
        OrderResponseDTO response=orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }
}