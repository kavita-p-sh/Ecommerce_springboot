package com.ecommerce.api.repository;
import com.ecommerce.api.entity.OrdersEntity;
import com.ecommerce.api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrdersEntity, Long> {

    List<OrdersEntity> findByUser(UserEntity user);

    List<OrdersEntity> findByStatus_StatusName(String statusName);

    List<OrdersEntity> findByCreatedBy(String createdBy);

    List<OrdersEntity> findByTotalAmount(BigDecimal totalAmount);

    List<OrdersEntity> findByTotalQuantity(Integer totalQuantity);


}