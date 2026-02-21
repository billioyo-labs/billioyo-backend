package com.itemrental.billioyo.global.error.exceptions;

public abstract class InvalidValueException extends BusinessException {
    public InvalidValueException(String message) {
        super(message);
    }
}
