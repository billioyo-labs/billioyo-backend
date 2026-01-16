package com.itemrental.rentalService.domain.order.repository;

import com.itemrental.rentalService.domain.order.entity.Order;
import com.itemrental.rentalService.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    int countByUserAndStatus(User user, Order.OrderStatus status);
}
