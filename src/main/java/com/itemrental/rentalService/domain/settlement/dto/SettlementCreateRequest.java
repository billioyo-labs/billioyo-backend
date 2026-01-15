package com.itemrental.rentalService.domain.settlement.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettlementCreateRequest {
    String bankName;          // 은행명
    String accountNumber;     // 계좌번호
    String accountHolder;     // 예금주명
}
