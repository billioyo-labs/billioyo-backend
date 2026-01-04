package com.itemrental.rentalService.domain.order.repository;

import com.itemrental.rentalService.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
