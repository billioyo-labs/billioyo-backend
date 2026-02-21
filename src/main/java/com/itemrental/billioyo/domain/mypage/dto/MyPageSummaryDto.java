package com.itemrental.billioyo.domain.mypage.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageSummaryDto {
    int rentedCount;
    int lentCount;
    Long settlementAmount;
}
