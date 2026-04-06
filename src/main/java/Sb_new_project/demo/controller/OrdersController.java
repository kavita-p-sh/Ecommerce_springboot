package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.OrderResponseDTO;
import Sb_new_project.demo.entity.OrderItemEntity;
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
     * create a new order
     * @param dto list of order items
     * @return order detail
     */
    @PostMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody List<OrderItemEntity>dto) {

        log.info("Order creation request");
        OrderResponseDTO response = orderService.createOrder(dto);

        log.info("Order created successfully with id {}", response.getOrderId());

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
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@RequestBody OrderResponseDTO filterDTO)  {
        log.info("Fetching orders :{}",filterDTO);
        return ResponseEntity.ok(orderService.getOrders(filterDTO));
    }

    /**
     * cacel order by id
     * @param orderId id of order
     * @return updated order after cancellation
     */
    @PutMapping("/cancel/{orderId}")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        log.warn("Cancelling order with id {}", orderId);
        OrderResponseDTO response=orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }
}