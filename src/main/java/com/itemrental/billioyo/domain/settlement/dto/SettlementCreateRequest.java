package com.itemrental.billioyo.domain.settlement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettlementCreateRequest {
    String bankName;          // 은행명
    String accountNumber;     // 계좌번호
    String accountHolder;     // 예금주명
}
