package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.OrderItemRequest;
import Sb_new_project.demo.dto.OrderRequestDTO;
import Sb_new_project.demo.dto.OrderResponseDTO;
import Sb_new_project.demo.entity.*;
import Sb_new_project.demo.enums.OrderStatusEnum;
import Sb_new_project.demo.exception.BadRequestException;
import Sb_new_project.demo.exception.ResourceNotFoundException;
import Sb_new_project.demo.repository.*;
import Sb_new_project.demo.service.OrderService;
import Sb_new_project.demo.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;


    private final LoggedInUserServiceImpl loggedInUserServiceImpl;


    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {


        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();


        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException(Constant.ORDER_ITEMS_CAN_NOT_EMPTY);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND));

        OrderStatus status = orderStatusRepository
                .findByStatusName(OrderStatusEnum.PLACED.name())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.STATUS_NOT_FOUND));

        Orders order = new Orders();
        order.setUser(user);
        order.setStatus(status);
        order.setCreatedBy(username);

        double totalAmount = 0;
        int totalQuantity = 0;

        for (OrderItemRequest item : request.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND));

            int qty = item.getQuantity();

            if (product.getQuantity() < qty) {
                throw new BadRequestException(Constant.OUT_OF_STOCK + product.getName());
            }

            totalAmount += product.getPrice() * qty;
            totalQuantity += qty;
        }

        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQuantity);


        order = orderRepository.save(order);
//        if(true)
//        throw new RuntimeException("Test rollback after order save");


        for (OrderItemRequest item : request.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND));

            int qty = item.getQuantity();

            int updatedQty = product.getQuantity() - qty;

            if (updatedQty < 0) {
                throw new BadRequestException(Constant.NOT_ENOUGH_STOCK+ product.getName());
            }

            product.setQuantity(updatedQty);
            product.setUpdatedBy(username);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);

            orderItemRepository.save(orderItem);
        }

        log.info("Order created successfully with ID: {}", order.getOrderId());

        return mapToResponse(order);
    }

        @Override
        public List<OrderResponseDTO> getOrdersByUser() {
        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND));

        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {

        log.info("Fetching all orders (Admin)");

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {

        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.ORDER_NOT_FOUND));

        if (!order.getUser().getUsername().equals(username)) {
            throw new BadRequestException(Constant.UNAUTHORIZED_ORDER_ACCESS);
        }

        return mapToResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {

        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.ORDER_NOT_FOUND));

        OrderStatus cancelledStatus = orderStatusRepository
                .findByStatusName(OrderStatusEnum.CANCELLED.name())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.STATUS_NOT_FOUND));

        OrderStatus deliveredStatus = orderStatusRepository
                .findByStatusName(OrderStatusEnum.DELIVERED.name())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.STATUS_NOT_FOUND));

        if (order.getStatus().equals(deliveredStatus)) {
            throw new BadRequestException(Constant.CANNOT_CANCEL);
        }

        List<OrderItem> items = orderItemRepository.findByOrder(order);

        for (OrderItem item : items) {

            Product product = item.getProduct();

            product.setQuantity(product.getQuantity() + 1);
            product.setUpdatedBy(username);
            throw new RuntimeException("Mid failure");

//            productRepository.save(product);

        }

        order.setStatus(cancelledStatus);
        order.setUpdatedBy(username);


        orderRepository.save(order);

        log.info("Order cancelled with ID: {}", id);
    }

    private OrderResponseDTO mapToResponse(Orders order) {

        OrderResponseDTO response = new OrderResponseDTO();

        response.setOrderId(order.getOrderId());
        response.setTotalAmount(order.getTotalAmount());
        response.setTotalQuantity(order.getTotalQuantity());
        response.setStatus(order.getStatus().getStatusName());
        response.setCreatedDate(order.getCreatedDate());

        return response;
    }
}