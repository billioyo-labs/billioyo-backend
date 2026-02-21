package com.itemrental.billioyo.domain.payment.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortOneDto {
    private String impUid;
    private String merchantUid;
    private Long amount;
    private Long orderId;
}
