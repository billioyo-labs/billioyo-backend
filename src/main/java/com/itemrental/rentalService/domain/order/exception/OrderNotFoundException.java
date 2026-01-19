package com.itemrental.rentalService.domain.order.exception;

import com.itemrental.rentalService.global.error.exceptions.EntityNotFoundException;

public class OrderNotFoundException extends EntityNotFoundException {
    public OrderNotFoundException(Long orderId) {
        super("주문을 찾을 수 없습니다. ID: " + orderId);
    }
}
