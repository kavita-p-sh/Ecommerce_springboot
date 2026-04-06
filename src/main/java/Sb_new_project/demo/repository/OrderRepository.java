package Sb_new_project.demo.repository;
import Sb_new_project.demo.entity.OrdersEntity;
import Sb_new_project.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrdersEntity, Long> {

    List<OrdersEntity> findByUser(UserEntity user);

    List<OrdersEntity> findByOrderStatus_StatusName(String status);

    List<OrdersEntity> findByCreatedBy(String createdBy);

    List<OrdersEntity> findByTotalAmount(BigDecimal totalAmount);

    List<OrdersEntity> findByTotalQuantity(Integer totalQuantity);
}