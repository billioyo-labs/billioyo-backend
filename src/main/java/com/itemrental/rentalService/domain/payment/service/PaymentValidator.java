package com.itemrental.rentalService.domain.payment.service;

import com.itemrental.rentalService.domain.payment.dto.PaymentInfo;

public interface PaymentValidator {
    PaymentInfo validate(String impUid);
}
