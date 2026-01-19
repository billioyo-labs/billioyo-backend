package com.itemrental.rentalService.domain.payment.exception;

import com.itemrental.rentalService.global.error.exceptions.InvalidValueException;

public class AlreadyPaidException extends InvalidValueException {
    public AlreadyPaidException() {
        super("이미 결제가 완료된 주문입니다.");
    }
}
