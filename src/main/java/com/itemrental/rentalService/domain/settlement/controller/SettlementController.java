package com.itemrental.rentalService.domain.settlement.controller;


import com.itemrental.rentalService.domain.settlement.dto.SettlementCreateRequest;
import com.itemrental.rentalService.domain.settlement.dto.SettlementCreateResponse;
import com.itemrental.rentalService.domain.settlement.dto.SettlementItemResponse;
import com.itemrental.rentalService.domain.settlement.service.SettlementService;
import com.itemrental.rentalService.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settlements")
@Tag(name = "Settlement", description = "정산 처리 API")
public class SettlementController {

    private final SettlementService settlementService;

    @Operation(
        summary = "정산 가능 목록 조회",
        description =
            "판매자가 마이페이지에서 정산 가능 금액과 정산 대상 내역을 조회할 때 호출되는 API입니다."
    )
    @GetMapping("/mypage/{ownerId}")
    public ResponseEntity<ApiResponse<SettlementItemResponse>> getSettlements(@PathVariable Long ownerId) {
        SettlementItemResponse result = settlementService.getSettlementItems(ownerId);
        return ResponseEntity.ok(ApiResponse.success("호출 성공",result));
    }

    @Operation(
        summary = "정산 요청",
        description =
            "판매자가 정산 요청 버튼을 클릭했을 때 호출되는 API입니다. 정산 테이블을 생성하고, 정산 대상 주문들을 하나의 정산으로 묶습니다."
    )
    @PostMapping("/{ownerId}")
    public ResponseEntity<ApiResponse<SettlementCreateResponse>> createSettlement(
        @PathVariable Long ownerId,
        @RequestBody SettlementCreateRequest dto) {
        SettlementCreateResponse result = settlementService.createSettlement(ownerId, dto);
        return ResponseEntity.ok(ApiResponse.success("정산 테이블 생성", result));
    }

    @Operation(
        summary = "정산 완료 처리",
        description =
            "관리자 또는 시스템에 의해 정산이 완료되었을 때 호출되는 API입니다. 정산 상태를 완료로 변경합니다."
    )
    @PostMapping("/{settlementId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeSettlement(@PathVariable Long settlementId) {
        settlementService.completeSettlement(settlementId);
        return ResponseEntity.ok(ApiResponse.success("정산 완료 처리 성공"));
    }

}
