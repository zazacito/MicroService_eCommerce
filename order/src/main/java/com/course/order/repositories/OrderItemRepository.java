package com.course.order.repositories;

import com.course.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<Order, Long> {
}
