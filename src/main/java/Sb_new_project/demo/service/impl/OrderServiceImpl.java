package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.OrderResponseDTO;
import Sb_new_project.demo.entity.*;
import Sb_new_project.demo.enums.OrderStatusEnum;
import Sb_new_project.demo.exception.BadRequestException;
import Sb_new_project.demo.exception.UserNotFoundException;
import Sb_new_project.demo.repository.*;
import Sb_new_project.demo.service.OrderService;
import Sb_new_project.demo.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public OrderResponseDTO createOrder(List<OrderItemEntity> request) {
        String userName = loggedInUserServiceImpl.getCurrentUser().getUsername();

        checkItems(request);
        UserEntity user = getUser(userName);
        OrderStatusEntity status = getStatus();
        OrdersEntity order = makeOrder(user, status, userName);
        addTotals(request, order);

        order = orderRepository.save(order);
        saveItems(request, order, userName);

        return mapToDTO(order);
    }

    /**
     * check items that item can not empty
     */
    private void checkItems(List<OrderItemEntity> items) {
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
    private UserEntity getUser(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND));
    }


    /**
     * Get Status of Order
     *
     * @return
     */
    private OrderStatusEntity getStatus() {
        Optional<OrderStatusEntity> status =
                orderStatusRepository.findByStatusName(OrderStatusEnum.PLACED.name());
        if (status.isPresent()) {
            return status.get();
        } else {
            throw new UserNotFoundException(Constant.STATUS_NOT_FOUND);
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
    private OrdersEntity makeOrder(UserEntity user, OrderStatusEntity status, String userName) {
        OrdersEntity order = new OrdersEntity();
        order.setUser(user);
        order.setStatus(status);
        order.setCreatedBy(userName);
        return order;
    }


    private void addTotals(List<OrderItemEntity> items, OrdersEntity order) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQty = 0;

        for (OrderItemEntity item : items) {
            if (item.getProduct() == null || item.getProduct().getProductId() == null) {
                throw new BadRequestException(Constant.PRODUCT_NOT_FOUND);
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BadRequestException(Constant.PRODUCT_QUANTITY_INVALID);
            }

            ProductEntity product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new UserNotFoundException(Constant.PRODUCT_NOT_FOUND));

            int quantity = item.getQuantity();

            if (product.getQuantity() < quantity) {
                throw new BadRequestException(Constant.OUT_OF_STOCK + product.getName());
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            totalAmount = totalAmount.add(itemTotal);

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
    private void saveItems(List<OrderItemEntity> items, OrdersEntity order, String userName) {

        for (OrderItemEntity item : items) {
            ProductEntity product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new UserNotFoundException(Constant.PRODUCT_NOT_FOUND));

            int quantity = item.getQuantity();

            product.setQuantity(product.getQuantity() - quantity);
            product.setUpdatedBy(userName);
            productRepository.save(product);

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(product);

            orderItem.setQuantity(quantity);

            orderItemRepository.save(orderItem);
        }
    }
    @Override
    public List<OrderResponseDTO> getOrders(OrderResponseDTO filterDTO) {

        log.info("Fetching orders with filters: {}", filterDTO);

        if (filterDTO.getStatus() != null && !filterDTO.getStatus().isBlank()) {
            log.info("Fetching orders by status: {}", filterDTO.getStatus());

            return orderRepository.findByOrderStatus_StatusName(filterDTO.getStatus())
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        if (filterDTO.getCreatedBy() != null && !filterDTO.getCreatedBy().isBlank()) {
            log.info("Fetching orders by createdBy: {}", filterDTO.getCreatedBy());

            return orderRepository.findByCreatedBy(filterDTO.getCreatedBy())
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        if (filterDTO.getTotalAmount() != null) {
            log.info("Fetching orders by totalAmount: {}", filterDTO.getTotalAmount());

            return orderRepository.findByTotalAmount(filterDTO.getTotalAmount())
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        if (filterDTO.getTotalQuantity() != null) {
            log.info("Fetching orders by totalQuantity: {}", filterDTO.getTotalQuantity());

            return orderRepository.findByTotalQuantity(filterDTO.getTotalQuantity())
                    .stream()
                    .map(this::mapToDTO)
                    .toList();
        }

        return getAllOrders();
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
     * Getting Orderby username
     *
     * @return
     */
    @Cacheable(value = "ordersByUser", key = "#currentUserOrders")
    @Override
    public List<OrderResponseDTO> getOrdersByUser() {

        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND));

        return orderRepository.findByUser(user)
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
    public OrderResponseDTO cancelOrder(Long orderId) {

        String username = loggedInUserServiceImpl.getCurrentUser().getUsername();

        OrdersEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new UserNotFoundException(Constant.ORDER_NOT_FOUND));

        if (!order.getUser().getUsername().equals(username)) {
            throw new BadRequestException(Constant.NOT_ALLOWED_TO_CANCEL_ORDER);
        } else {
            if (order.getStatus().getStatusName().equals(OrderStatusEnum.DELIVERED.name())) {
                throw new BadRequestException(Constant.CANNOT_CANCEL);
            }
        }

        OrderStatusEntity cancelledStatus = orderStatusRepository
                .findByStatusName(OrderStatusEnum.CANCELLED.name())
                .orElseThrow(() -> new UserNotFoundException(Constant.STATUS_NOT_FOUND));

        List<OrderItemEntity> items = orderItemRepository.findByOrder(order);

        for (OrderItemEntity item : items) {
            ProductEntity product = item.getProduct();
            product.setQuantity(product.getQuantity() + 1);
            product.setUpdatedBy(username);

            productRepository.save(product);
        }

        order.setStatus(cancelledStatus);
        order.setUpdatedBy(username);

        orderRepository.save(order);

        log.info("Order cancelled with id: {} by user: {}", orderId, username);

        return mapToDTO(order);
    }

    /**
     * Converts Orders entity into OrderResponseDTO
     *
     * @param order
     * @return
     */
    private OrderResponseDTO mapToDTO(OrdersEntity order) {
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