package com.itemrental.billioyo.domain.order.exception;

import com.itemrental.billioyo.global.error.exceptions.EntityNotFoundException;

public class OrderNotFoundException extends EntityNotFoundException {
    public OrderNotFoundException(Long orderId) {
        super("주문을 찾을 수 없습니다. ID: " + orderId);
    }
}
