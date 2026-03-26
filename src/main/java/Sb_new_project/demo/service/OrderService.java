package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.OrderRequestDTO;
import Sb_new_project.demo.dto.OrderResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional
    OrderResponseDTO createOrder(OrderRequestDTO request);

    List<OrderResponseDTO> getOrdersByUser();

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(Long id);

    @Transactional
    void cancelOrder(Long id);
}
