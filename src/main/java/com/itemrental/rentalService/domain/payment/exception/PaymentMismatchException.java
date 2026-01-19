package com.itemrental.rentalService.domain.payment.exception;

import com.itemrental.rentalService.global.error.exceptions.InvalidValueException;

public class PaymentMismatchException extends InvalidValueException {
    public PaymentMismatchException(String detail) {
        super("결제 정보가 일치하지 않습니다: " + detail);
    }
}
