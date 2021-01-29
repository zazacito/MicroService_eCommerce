package com.course.order.repositories;

import com.course.order.domain.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<Commande, Long> {
}
