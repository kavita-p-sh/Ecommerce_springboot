package Sb_new_project.demo.repository;
import Sb_new_project.demo.entity.OrderItemEntity;
import Sb_new_project.demo.entity.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByOrder(OrdersEntity order);
}