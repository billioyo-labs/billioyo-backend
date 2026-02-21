package com.itemrental.billioyo.domain.payment.service;

import com.itemrental.billioyo.domain.payment.dto.PaymentInfo;

public interface PaymentValidator {
    PaymentInfo validate(String impUid);
}
