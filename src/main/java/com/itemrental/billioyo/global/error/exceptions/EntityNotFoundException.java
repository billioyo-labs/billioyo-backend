package com.itemrental.billioyo.global.error.exceptions;

public abstract class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
