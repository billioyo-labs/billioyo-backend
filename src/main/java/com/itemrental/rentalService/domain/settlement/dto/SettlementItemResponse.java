package com.itemrental.rentalService.domain.settlement.dto;

import com.itemrental.rentalService.domain.settlement.entity.SettlementItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SettlementItemResponse {
    List<SettlementItem> items;
    Long totalAmount;
}
