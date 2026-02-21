package com.itemrental.billioyo.domain.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettlementCreateResponse {
    Long settlementId;
    Long totalAmount;
}
