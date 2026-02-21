package com.itemrental.billioyo.domain.order.repository;

import com.itemrental.billioyo.domain.order.entity.Order;
import com.itemrental.billioyo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    int countByUserAndStatus(User user, Order.OrderStatus status);
    Page<Order> findByUserAndStatus(User user, Order.OrderStatus status, Pageable pageable);
}
