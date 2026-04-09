package com.ecommerce.api.repository;
import com.ecommerce.api.entity.OrderItemEntity;
import com.ecommerce.api.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByOrder(OrdersEntity order);
}