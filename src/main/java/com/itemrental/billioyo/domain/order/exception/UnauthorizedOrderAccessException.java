package com.itemrental.billioyo.domain.order.exception;

import com.itemrental.billioyo.global.error.exceptions.ForbiddenException;

public class UnauthorizedOrderAccessException extends ForbiddenException {
    public UnauthorizedOrderAccessException() {
        super("해당 주문에 대한 처리 권한이 없습니다.");
    }
}
