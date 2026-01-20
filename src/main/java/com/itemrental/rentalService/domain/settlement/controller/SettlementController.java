package com.itemrental.rentalService.domain.settlement.controller;


import com.itemrental.rentalService.domain.settlement.dto.SettlementCreateRequest;
import com.itemrental.rentalService.domain.settlement.dto.SettlementCreateResponse;
import com.itemrental.rentalService.domain.settlement.dto.SettlementItemResponse;
import com.itemrental.rentalService.domain.settlement.service.SettlementService;
import com.itemrental.rentalService.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    // 정산 가능 목록 + 합계 조회 (마이페이지)
    // GET /settlements/mypage/{ownerId}
    @GetMapping("/mypage/{ownerId}")
    public ResponseEntity<ApiResponse<SettlementItemResponse>> getSettlements(@PathVariable Long ownerId) {
        SettlementItemResponse result = settlementService.getSettlementItems(ownerId);
        return ResponseEntity.ok(ApiResponse.success("호출 성공",result));
    }

    // 정산 요청(정산 테이블 생성 + SettlementItem에 settlementId 묶기)
    // POST /settlements/{ownerId}
    @PostMapping("/{ownerId}")
    public ResponseEntity<ApiResponse<SettlementCreateResponse>> createSettlement(
        @PathVariable Long ownerId,
        @RequestBody SettlementCreateRequest dto) {
        SettlementCreateResponse result = settlementService.createSettlement(ownerId, dto);
        return ResponseEntity.ok(ApiResponse.success("정산 테이블 생성", result));
    }

    // 정산 완료 처리
    // POST /settlements/{settlementId}/complete
    @PostMapping("/{settlementId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeSettlement(@PathVariable Long settlementId) {
        settlementService.completeSettlement(settlementId);
        return ResponseEntity.ok(ApiResponse.success("정산 완료 처리 성공"));
    }

}
