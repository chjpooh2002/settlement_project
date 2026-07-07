package com.example.settlement.domain.order.repository;
import com.example.settlement.domain.order.entity.OrderProduct;
import com.example.settlement.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findAllByOrdersOrderStatus(OrderStatus orderStatus);
}
