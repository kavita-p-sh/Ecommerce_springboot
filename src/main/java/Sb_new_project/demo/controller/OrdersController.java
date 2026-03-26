package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.OrderRequestDTO;
import Sb_new_project.demo.dto.OrderResponseDTO;
import Sb_new_project.demo.service.OrderService;
import Sb_new_project.demo.service.LoggedInUserService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final OrderService orderService;
    private final LoggedInUserService loggedInUserService;


    @PostMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO dto) {

        log.info("Order creation request");

        OrderResponseDTO response = orderService.createOrder(dto);

        log.info("Order created successfully with id {}", response.getOrderId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {

        log.info("Fetching orders");

        if (loggedInUserService.isAdmin()) {
            return ResponseEntity.ok(orderService.getAllOrders());
        } else {
            return ResponseEntity.ok(orderService.getOrdersByUser());
        }
    }

    @GetMapping("/{id}")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {

        log.info("Fetching order with id: {}", id);

        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}/cancel")
    @RolesAllowed(Constant.ROLE_USER)
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {

        log.warn("Cancelling order with id: {}", id);

        orderService.cancelOrder(id);

        return ResponseEntity.ok("Order " + id + Constant.CANCELLED);
    }
}