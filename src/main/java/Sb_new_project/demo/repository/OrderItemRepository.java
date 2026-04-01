package Sb_new_project.demo.repository;

import Sb_new_project.demo.entity.OrderItem;
import Sb_new_project.demo.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Orders order);
}