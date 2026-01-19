package com.itemrental.rentalService.domain.order.exception;

import com.itemrental.rentalService.global.error.exceptions.ForbiddenException;

public class UnauthorizedOrderAccessException extends ForbiddenException {
    public UnauthorizedOrderAccessException() {
        super("해당 주문에 대한 처리 권한이 없습니다.");
    }
}
