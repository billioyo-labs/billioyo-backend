package com.itemrental.rentalService.domain.order.exception;

import com.itemrental.rentalService.global.error.exceptions.InvalidValueException;

public class AlreadyRentedException extends InvalidValueException {
    public AlreadyRentedException() {
        super("이미 대여 중인 물품입니다.");
    }
}
