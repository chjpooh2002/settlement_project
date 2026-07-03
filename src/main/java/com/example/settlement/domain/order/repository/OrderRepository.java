package com.example.settlement.domain.order.repository;

import com.example.settlement.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
