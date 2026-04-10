package com.ecommerce.api.repository;
import com.ecommerce.api.entity.OrderEntity;
import com.ecommerce.api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUser(UserEntity user);

    List<OrderEntity> findByStatus_StatusName(String statusName);

    List<OrderEntity> findByCreatedBy(String createdBy);

    List<OrderEntity> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<OrderEntity> findByTotalQuantityGreaterThanEqual(Integer minQuantity);


}