package com.itemrental.rentalService.domain.order.controller;

import com.itemrental.rentalService.domain.order.dto.OrderCreateRequestDto;
import com.itemrental.rentalService.domain.order.dto.OrderCreateResponseDto;
import com.itemrental.rentalService.domain.order.service.OrderService;
import com.itemrental.rentalService.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "주문/결제 관련 API")
public class OrderController {
    private final OrderService orderService;

    @Operation(
        summary = "주문 생성",
        description =
            "사용자가 결제하기 버튼을 클릭했을 때 호출되는 API입니다. 결제 요청 정보를 기반으로 주문을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponseDto>> createOrder(
            @RequestBody OrderCreateRequestDto orderCreateRequestDto,
            Principal principal) {

        String email = principal.getName();
        OrderCreateResponseDto dto = orderService.createOrder(orderCreateRequestDto, email);

        return ResponseEntity.ok(ApiResponse.success("주문 생성 완료", dto));
    }
}
