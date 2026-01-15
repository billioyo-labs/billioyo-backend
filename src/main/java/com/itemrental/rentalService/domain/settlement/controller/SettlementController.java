package com.itemrental.rentalService.domain.settlement.controller;


import com.itemrental.rentalService.domain.settlement.dto.SettlementCreateRequest;
import com.itemrental.rentalService.domain.settlement.dto.SettlementCreateResponse;
import com.itemrental.rentalService.domain.settlement.dto.SettlementItemResponse;
import com.itemrental.rentalService.domain.settlement.service.SettlementService;
import com.itemrental.rentalService.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @GetMapping("/mypage/{ownerId}")
    public ApiResponse<SettlementItemResponse>  getSettlements(@PathVariable Long ownerId) {
        SettlementItemResponse result = settlementService.getSettlementItems(ownerId);
        return ApiResponse.success("호출 성공",result);
    }

    @PostMapping("/settlement/{ownerId}")
    public ApiResponse<SettlementCreateResponse> createSettlement(@PathVariable Long ownerId, SettlementCreateRequest dto) {
        SettlementCreateResponse result = settlementService.createSettlement(ownerId, dto);
        return ApiResponse.success("정산 테이블 생성", result);
    }

}
