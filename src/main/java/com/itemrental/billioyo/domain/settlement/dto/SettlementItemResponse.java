package com.itemrental.billioyo.domain.settlement.dto;

import com.itemrental.billioyo.domain.settlement.entity.SettlementItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SettlementItemResponse {
    List<SettlementItem> items;
    Long totalAmount;
}
