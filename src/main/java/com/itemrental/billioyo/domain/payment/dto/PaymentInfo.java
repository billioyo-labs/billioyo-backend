package com.itemrental.billioyo.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentInfo {
    private String impUid;
    private String merchantUid;
    private long amount;
    private String status;
    private String payMethod;
    private String pgProvider;
    private String name;
}
