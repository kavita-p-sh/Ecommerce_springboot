package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.OrderItemRequest;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final LoggedInUserServiceImpl loggedInUserServiceImpl;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * creating order
     */
    @Override
    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public OrderResponseDTO createOrder(List<OrderItemRequest> request) {
        String userName = loggedInUserServiceImpl.getCurrentUser().getUsername();

        checkItems(request);
        User user = getUser(userName);
        OrderStatus status = getStatus();
        Orders order = makeOrder(user, status, userName);
        addTotals(request, order);

        order = orderRepository.save(order);
        saveItems(request, order, userName);

        return mapToDTO(order);
    }

    /**
     * check items that item can not empty
     */
    private void checkItems(List<OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new BadRequestException(Constant.ORDER_ITEMS_CAN_NOT_EMPTY);
        }
    }

    /**
     * get user for Order
     *
     * @param userName
     * @return
     */
    private User getUser(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND));
    }


    /**
     * Get Status of Order
     *
     * @return
     */
    private OrderStatus getStatus() {
        Optional<OrderStatus> status =
                orderStatusRepository.findByStatusName(OrderStatusEnum.PLACED.name());
        if (status.isPresent()) {
            return status.get();
        } else {
            throw new ResourceNotFoundException(Constant.STATUS_NOT_FOUND);
        }
    }

    /**
     * final order
     *
     * @param user
     * @param status
     * @param userName
     * @return
     */
    private Orders makeOrder(User user, OrderStatus status, String userName) {
        Orders order = new Orders();
        order.setUser(user);
        order.setStatus(status);
        order.setCreatedBy(userName);
        return order;
    }


    private void addTotals(List<OrderItemRequest> items, Orders order) {
        double totalAmount = 0;
        int totalQty = 0;
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND));

            int quantity = item.getQuantity();

            if (product.getQuantity() < quantity) {
                throw new BadRequestException(Constant.OUT_OF_STOCK + product.getName());
            }

            totalAmount = totalAmount + product.getPrice() * quantity;
            totalQty = totalQty + quantity;
        }

        order.setTotalAmount(totalAmount);
        order.setTotalQuantity(totalQty);
    }

    /**
     * Save OrderItem
     *
     * @param order
     * @param userName
     */
    private void saveItems(List<OrderItemRequest> items, Orders order, String userName) {

        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constant.PRODUCT_NOT_FOUND));

            int quantity = item.getQuantity();

            product.setQuantity(product.getQuantity() - quantity);
            product.setUpdatedBy(userName);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);

            orderItemRepository.save(orderItem);
        }
    }

    /**
     * Getting Orderby username
     *
     * @return
     */
    @Cacheable(value = "ordersByUser", key = "#currentUserOrders")
    @Override
    public List<OrderResponseDTO> getOrdersByUser() {

        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND));

        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * get All oders
     *
     * @return
     */
    @Cacheable(value = "orders", key = "'AllOrders'")
    @Override
    public List<OrderResponseDTO> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Cancel the order
     *
     * @param orderId
     */
    @Override
    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void cancelOrder(Long orderId) {

        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.ORDER_NOT_FOUND));

        if (!order.getUser().getUsername().equals(username)) {
            throw new BadRequestException(Constant.NOT_ALLOWED_TO_CANCEL_ORDER);
        } else {
            if (order.getStatus().getStatusName().equals(OrderStatusEnum.DELIVERED.name())) {
                throw new BadRequestException(Constant.CANNOT_CANCEL);
            }
        }

        OrderStatus cancelledStatus = orderStatusRepository

                .findByStatusName(OrderStatusEnum.CANCELLED.name())
                .orElseThrow(() -> new ResourceNotFoundException(Constant.STATUS_NOT_FOUND));

        List<OrderItem> items = orderItemRepository.findByOrder(order);

        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + 1);
            product.setUpdatedBy(username);

            productRepository.save(product);
        }

        order.setStatus(cancelledStatus);
        order.setUpdatedBy(username);

        orderRepository.save(order);

        log.info("Order cancelled", orderId, username);
    }

    /**
     * Converts Orders entity into OrderResponseDTO
     *
     * @param order
     * @return
     */
    private OrderResponseDTO mapToDTO(Orders order) {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getOrderId());
        response.setTotalAmount(order.getTotalAmount());
        response.setTotalQuantity(order.getTotalQuantity());
        response.setStatus(order.getStatus().getStatusName());
        response.setCreatedBy(order.getCreatedBy());
        response.setCreatedTimestamp(order.getCreatedTimestamp());
        response.setUpdatedTimeStamp(order.getUpdatedTimestamp());
        return response;
    }
}