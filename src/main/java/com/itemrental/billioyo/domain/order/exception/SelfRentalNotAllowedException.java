package com.itemrental.billioyo.domain.order.exception;

import com.itemrental.billioyo.global.error.exceptions.InvalidValueException;

public class SelfRentalNotAllowedException extends InvalidValueException {
    public SelfRentalNotAllowedException() {
        super("본인이 등록한 물품은 대여할 수 없습니다.");
    }
}
