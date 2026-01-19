package com.itemrental.rentalService.global.error.exceptions;

public abstract class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
