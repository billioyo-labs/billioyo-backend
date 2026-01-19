package com.itemrental.rentalService.global.error.exceptions;

public abstract class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(message);
    }
}
